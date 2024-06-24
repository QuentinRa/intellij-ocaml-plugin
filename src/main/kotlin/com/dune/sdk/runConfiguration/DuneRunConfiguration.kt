// Copyright 2000-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.dune.sdk.runConfiguration

import com.dune.DuneBundle.message
import com.dune.icons.DuneIcons
import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.execution.configurations.*
import com.intellij.execution.configurations.ConfigurationTypeUtil.findConfigurationType
import com.intellij.execution.configurations.GeneralCommandLine.ParentEnvironmentType
import com.intellij.execution.process.ColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.wsl.WSLCommandLineOptions
import com.intellij.execution.wsl.WslPath
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PathMacroManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.encoding.EncodingManager
import com.intellij.util.EnvironmentUtil
import com.intellij.util.concurrency.annotations.RequiresEdt
import com.jetbrains.lang.makefile.*
import org.jdom.Element
import java.nio.file.Paths
import com.intellij.execution.configuration.EnvironmentVariablesComponent
import com.intellij.icons.AllIcons
import com.intellij.openapi.application.PathMacros
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.*
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiManager
import com.intellij.ui.*
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.UIUtil
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.DocumentEvent

class DuneRunConfiguration(project: Project, factory: DuneRunConfigurationFactory, name: String) : LocatableConfigurationBase<RunProfileState>(project, factory, name) {
    var filename: String = ""
    var target: String = ""
    var workingDirectory: String = ""
    var environmentVariables: EnvironmentVariablesData = EnvironmentVariablesData.DEFAULT
    var arguments: String = ""

    private companion object {
        private val LOGGER = logger<DuneRunConfiguration>()

        const val DUNE_KEY = "dune"
        const val FILENAME = "filename"
        const val TARGET = "target"
        const val WORKING_DIRECTORY = "workingDirectory"
        const val ARGUMENTS = "arguments"

        private const val SWITCH_FILE = "-f"

        private const val SWITCH_DIRECTORY = "-C"
    }

    override fun checkConfiguration() {
    }

    override fun getConfigurationEditor() = DuneRunConfigurationEditor(project)

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        val child = element.getOrCreateChild(DUNE_KEY)
        child.setAttribute(FILENAME, filename)
        child.setAttribute(TARGET, target)
        child.setAttribute(WORKING_DIRECTORY, workingDirectory)
        child.setAttribute(ARGUMENTS, arguments)
        environmentVariables.writeExternal(child)
    }

    override fun readExternal(element: Element) {
        super.readExternal(element)
        val child = element.getChild(DUNE_KEY)
        if (child != null) {
            filename = child.getAttributeValue(FILENAME) ?: ""
            target = child.getAttributeValue(TARGET) ?: ""
            workingDirectory = child.getAttributeValue(WORKING_DIRECTORY) ?: ""
            arguments = child.getAttributeValue(ARGUMENTS) ?: ""
            environmentVariables = EnvironmentVariablesData.readExternal(child)
        }
    }

    @RequiresEdt
    override fun getState(executor: Executor, executionEnvironment: ExecutionEnvironment): RunProfileState {
        val makeSettings = project.getService(MakefileProjectSettings::class.java).settings
        val makePath = makeSettings?.path ?: DEFAULT_MAKE_PATH
        val useCygwin = makeSettings?.useCygwin ?: false

        return object : CommandLineState(executionEnvironment) {
            override fun startProcess(): ProcessHandler {
                val cmd = newCommandLine(makePath, useCygwin)

                val processHandler = ColoredProcessHandler(cmd)
                processHandler.setShouldKillProcessSoftly(true)
                ProcessTerminatedListener.attach(processHandler)
                return processHandler
            }
        }
    }

    /**
     * WSL-specific corner cases (Windows 10+):
     *
     * - Non-WSL paths (Cygwin, MinGW): the remote _Make_ path is `null`.
     * - Missing WSL distributions (`\\wsl$\Missing`): the remote _Make_ path is non-`null`
     *   (an exception will be thrown later).
     * - WSL not installed (no `wsl.exe` in `%PATH%`): the remote _Make_ path is non-`null`
     *   (an exception will be thrown later).
     *
     * @throws ExecutionException if WSL is requested but is not installed, or the
     *   requested Linux distribution is missing.
     */
    @RequiresEdt
    @Throws(ExecutionException::class)
    private fun newCommandLine(localMakePath: String, useCygwin: Boolean): GeneralCommandLine =
        when (val remoteMakePath = WslPath.parseWindowsUncPath(windowsUncPath = localMakePath)) {
            null -> newCommandLineLocal(localMakePath, useCygwin)
            else -> newCommandLineWsl(remoteMakePath)
        }

    @RequiresEdt
    private fun newCommandLineLocal(localMakePath: String, useCygwin: Boolean): GeneralCommandLine {
        val macroManager = PathMacroManager.getInstance(project)

        val localMakefile = macroManager.expandPath(filename)

        val localWorkDirectory = when {
            workingDirectory.isNotEmpty() -> macroManager.expandPath(workingDirectory)
            else -> Paths.get(localMakefile).parent?.toString()
        }

        val makeSwitches = makeSwitches(localMakefile, localWorkDirectory)

        val environment = environment()
        var command = arrayOf(localMakePath) + makeSwitches.array
        command = customizeCommandAndEnvironment(command, environment)

        return command.toCommandLine(
            ::PtyCommandLine,
            localWorkDirectory,
            environment,
            useCygwin)
    }

    /**
     * @throws ExecutionException if WSL is not installed, or the requested Linux
     *   distribution is missing.
     */
    @RequiresEdt
    @Throws(ExecutionException::class)
    private fun newCommandLineWsl(remoteMakePath: WslPath): GeneralCommandLine {
        val distribution = remoteMakePath.distribution

        /*-
         * It is possible to set a non-root default user on a per-distribution
         * basis by creating a /etc/wsl.conf file with the following content:
         *
         * [user]
         * default=alice
         *
         * and restarting the guest VM. For details, see
         * <https://docs.microsoft.com/en-us/windows/wsl/wsl-config#user>.
         *
         * To facilitate debugging file access problems (when a regular user tries
         * to modify a file created by root), we should at least log the user's home,
         * or, better, display it in the event log (once per WSL distribution).
         */
        LOGGER.debugInBackground {
            @Suppress("LongLine")
            "The current user's home within the ${distribution.msId} WSL distribution is ${distribution.userHome}. Edit /etc/wsl.conf to change the default user."
        }

        val macroManager = PathMacroManager.getInstance(project)

        val localMakefile = macroManager.expandPath(filename)
        val remoteMakefile = distribution.getWslPath(localMakefile)

        val localWorkDirectory = when {
            workingDirectory.isNotEmpty() -> macroManager.expandPath(workingDirectory)
            else -> Paths.get(localMakefile).parent?.toString()
        }
        /*
         * Non-null as long as the local one is non-null, even if incorrect
         * distribution is passed or `wsl.exe` goes missing.
         */
        val remoteWorkDirectory = localWorkDirectory?.let(distribution::getWslPath)

        val makeSwitches = makeSwitches(remoteMakefile, remoteWorkDirectory)

        val environment = environment()
        var command = arrayOf(remoteMakePath.linuxPath) + makeSwitches.array
        command = customizeCommandAndEnvironment(command, environment)

        /*-
         * Two reasons for using a GeneralCommandLine here:
         *
         * 1. the child `wsl.exe` process may terminate with
         *    "The COM+ registry database detected a system error" message;
         * 2. the output of Make may be wrapped at 80th column, having extra
         *    line breaks where it shouldn't.
         */
        val localCommandLine = command.toCommandLine(
            ::GeneralCommandLine,
            localWorkDirectory,
            environment,
            useCygwinLaunch = false)

        /*
         * Currently, setting the remote work directory has no effect if
         * `executeCommandInShell` is `false` (otherwise it would simply result in
         * running `/bin/sh -c cd .. && make`).
         */
        val wslOptions = WSLCommandLineOptions()
            .setLaunchWithWslExe(true)
            .setExecuteCommandInShell(false)
            .setRemoteWorkingDirectory(remoteWorkDirectory)
            .setPassEnvVarsUsingInterop(true)

        return distribution.patchCommandLine(localCommandLine,
            project,
            wslOptions)
    }

    private fun makeSwitches(makefile: String?,
                             workDirectory: String?): ParametersList {
        val makeSwitches = ParametersList()

        if (makefile != null) {
            makeSwitches.addAll(SWITCH_FILE, makefile)
        }

        if (workDirectory != null) {
            makeSwitches.addAll(SWITCH_DIRECTORY, workDirectory)
        }

        /*
         * Pass extra arguments *after* -f/-C so that the user can override those.
         */
        makeSwitches.addParametersString(arguments)

        if (target.isNotEmpty()) {
            makeSwitches.addParametersString(target)
        }

        return makeSwitches
    }

    private fun environment(): MutableMap<String, String> {
        val parentEnvironment = when {
            environmentVariables.isPassParentEnvs -> EnvironmentUtil.getEnvironmentMap()
            else -> emptyMap()
        }
        return (parentEnvironment + environmentVariables.envs).toMutableMap()
    }

    /**
     * Starting `wsl.exe` with an empty parent environment ([ParentEnvironmentType.NONE])
     * **and** an empty (or almost empty) own environment (i. e. when the "Include
     * system environment variables" box is un-checked in the run configuration
     * settings) while also using a [PtyCommandLine] (instead of a regular
     * [GeneralCommandLine]) results in the process failure, with the only line
     * logged to stdout:
     *
     * > The COM+ registry database detected a system error
     *
     * Of course, we can switch to [ParentEnvironmentType.CONSOLE] (which is
     * enough for WSL), but that defies the whole purpose of having a checkbox in
     * the UI.
     *
     * @param commandLineInit the no-arg constructor of either [GeneralCommandLine]
     *   or any of its descendants.
     * @see ParentEnvironmentType
     * @see GeneralCommandLine
     * @see PtyCommandLine
     */
    private fun Array<String>.toCommandLine(commandLineInit: () -> GeneralCommandLine,
                                                     workDirectory: String?,
                                                     environment: Map<String, String>,
                                                     useCygwinLaunch: Boolean): GeneralCommandLine =
        commandLineInit()
            .withUseCygwinLaunchEx(useCygwinLaunch)
            .withExePath(this[0])
            .withWorkDirectory(workDirectory)
            .withEnvironment(environment)
            .withParentEnvironmentType(ParentEnvironmentType.NONE)
            .withParameters(slice(1 until size))
            .withCharset(EncodingManager.getInstance().defaultConsoleEncoding)

    /**
     * A useful extension which works uniformly for both [PtyCommandLine] and
     * [GeneralCommandLine].
     *
     * @see PtyCommandLine.withUseCygwinLaunch
     */
    private fun GeneralCommandLine.withUseCygwinLaunchEx(useCygwinLaunch: Boolean) =
        when (this) {
            is PtyCommandLine -> withUseCygwinLaunch(useCygwinLaunch)
            else -> this
        }

    @RequiresEdt
    private fun customizeCommandAndEnvironment(command: Array<String>,
                                               environment: MutableMap<String, String>): Array<String> {
        return command
    }

    @RequiresEdt
    private inline fun Logger.debugInBackground(crossinline lazyMessage: () -> String) {
        if (isDebugEnabled) {
            ApplicationManager.getApplication().executeOnPooledThread {
                debug(lazyMessage())
            }
        }
    }
}

class DuneRunConfigurationFactory(private val runConfigurationType: DuneRunConfigurationType) : ConfigurationFactory(runConfigurationType) {
    override fun getId(): String = runConfigurationType.id
    override fun getName(): String = runConfigurationType.displayName

    override fun createTemplateConfiguration(project: Project) = DuneRunConfiguration(project, this, "name")
}

class DuneRunConfigurationType : ConfigurationType {
    override fun getDisplayName(): String = message("run.configuration.name")
    override fun getConfigurationTypeDescription(): String = message("run.configuration.description")
    override fun getIcon() = DuneIcons.Nodes.DUNE
    override fun getId() = "DUNE_TARGET_RUN_CONFIGURATION"
    override fun getConfigurationFactories() = arrayOf(DuneRunConfigurationFactory(this))

    companion object {
        @JvmStatic
        val instance: DuneRunConfigurationType
            get() = findConfigurationType(DuneRunConfigurationType::class.java)
    }
}

class DuneRunConfigurationEditor(private val project: Project) : SettingsEditor<DuneRunConfiguration>() {
    private val filenameField = TextFieldWithBrowseButton()
    private val targetCompletionProvider = TextFieldWithAutoCompletion.StringsCompletionProvider(emptyList(), AllIcons.RunConfigurations.TestState.Run)
    private val targetField = TextFieldWithAutoCompletion(project, targetCompletionProvider, true, "")
    private val argumentsField = ExpandableTextField()
    private val workingDirectoryField = TextFieldWithBrowseButton()
    private val environmentVarsComponent = EnvironmentVariablesComponent()

    private val panel: JPanel by lazy {
        FormBuilder.createFormBuilder()
            .setAlignLabelOnRight(false)
            .setHorizontalGap(UIUtil.DEFAULT_HGAP)
            .setVerticalGap(UIUtil.DEFAULT_VGAP)
            .addLabeledComponent(MakefileLangBundle.message("run.configuration.editor.filename.label"), filenameField)
            .addLabeledComponent(MakefileLangBundle.message("run.configuration.editor.target.label"), targetField)
            .addComponent(LabeledComponent.create(argumentsField, MakefileLangBundle.message("run.configuration.editor.arguments.label")))
            .addLabeledComponent(MakefileLangBundle.message("run.configuration.editor.working.directory.label"), createComponentWithMacroBrowse(workingDirectoryField))
            .addComponent(environmentVarsComponent)
            .panel
    }

    init {
        filenameField.addBrowseFolderListener(
            MakefileLangBundle.message("file.chooser.title"),
            MakefileLangBundle.message("file.chooser.description"),
            project,
            MakefileFileChooserDescriptor()
        )
        filenameField.textField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(event: DocumentEvent) {
                updateTargetCompletion(filenameField.text)
            }
        })
        workingDirectoryField.addBrowseFolderListener(
            MakefileLangBundle.message("working.directory.file.chooser"),
            MakefileLangBundle.message("working.directory.file.chooser.description"),
            project,
            FileChooserDescriptorFactory.createSingleFolderDescriptor())
    }

    fun updateTargetCompletion(filename: String) {
        val file = LocalFileSystem.getInstance().findFileByPath(filename)
        if (file != null) {
            val psiFile = PsiManager.getInstance(project).findFile(file)
            if (psiFile != null) {
                targetCompletionProvider.setItems(findTargets(psiFile).map { it.name })
                return
            }
        }
        targetCompletionProvider.setItems(emptyList())
    }

    override fun createEditor() = panel

    override fun applyEditorTo(configuration: DuneRunConfiguration) {
        configuration.filename = filenameField.text
        configuration.target = targetField.text
        configuration.workingDirectory = workingDirectoryField.text
        configuration.environmentVariables = environmentVarsComponent.envData
        configuration.arguments = argumentsField.text
    }

    override fun resetEditorFrom(configuration: DuneRunConfiguration) {
        filenameField.text = configuration.filename
        targetField.text = configuration.target
        workingDirectoryField.text = configuration.workingDirectory
        environmentVarsComponent.envData = configuration.environmentVariables
        argumentsField.text = configuration.arguments

        updateTargetCompletion(configuration.filename)
    }


    // copied & converted to Kotlin from com.intellij.execution.ui.CommonProgramParametersPanel
    private fun createComponentWithMacroBrowse(textAccessor: TextFieldWithBrowseButton): JComponent {
        val button = FixedSizeButton(textAccessor)
        button.icon = AllIcons.Actions.ListFiles
        button.addActionListener {
            JBPopupFactory.getInstance().createPopupChooserBuilder(PathMacros.getInstance().userMacroNames.toList()).setItemChosenCallback { item: String ->
                textAccessor.text = "$$item$"
            }.setMovable(false).setResizable(false).createPopup().showUnderneathOf(button)
        }

        return JPanel(BorderLayout()).apply {
            add(textAccessor, BorderLayout.CENTER)
            add(button, BorderLayout.EAST)
        }
    }
}