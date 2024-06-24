// Copyright 2000-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.dune.sdk.runConfiguration

import com.dune.DuneBundle
import com.dune.icons.DuneIcons
import com.dune.ide.files.DuneFileType
import com.intellij.execution.ExecutionBundle
import com.intellij.execution.Executor
import com.intellij.execution.configuration.EnvironmentVariablesComponent
import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.execution.configurations.*
import com.intellij.execution.configurations.ConfigurationTypeUtil.findConfigurationType
import com.intellij.execution.process.ColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.icons.AllIcons
import com.intellij.openapi.application.PathMacros
import com.intellij.openapi.components.PathMacroManager
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.fileChooser.FileElement
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.FixedSizeButton
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorTextField
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.util.EnvironmentUtil
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.UIUtil
import com.ocaml.OCamlBundle
import com.ocaml.sdk.providers.OCamlSdkProvidersManager
import com.ocaml.sdk.utils.OCamlSdkIDEUtils
import org.jdom.Element
import java.awt.BorderLayout
import java.io.File
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Paths
import javax.swing.JComponent
import javax.swing.JPanel

class DuneRunConfiguration(project: Project, factory: DuneRunConfigurationFactory, name: String) : LocatableConfigurationBase<RunProfileState>(project, factory, name) {
    var duneFile: String = ""
    var target: String = ""
    var moduleName: String = ""
    var workingDirectory: String = defaultWorkingDirectory()
    var environmentVariables: EnvironmentVariablesData = EnvironmentVariablesData.DEFAULT
    var arguments: String = ""

    private fun defaultWorkingDirectory() : String = project.basePath.toString()

    private companion object {
        const val DUNE_KEY = "dune"
        const val FILENAME = "filename"
        const val TARGET = "target"
        const val WORKING_DIRECTORY = "workingDirectory"
        const val ARGUMENTS = "arguments"
        const val MODULE_NAME = "module"
    }

    override fun checkConfiguration() {
        // Check that the dune file exists
        val pathMacroManager = PathMacroManager.getInstance(project)
        val duneFilePath = pathMacroManager.expandPath(duneFile)
        var exists = try {
            Files.exists(Paths.get(duneFilePath))
        } catch (e: InvalidPathException) {
            false
        }
        if (!exists) {
            throw RuntimeConfigurationWarning(DuneBundle.message("dialog.message.dune.file.doesn.t.exist", duneFilePath))
        }

        // Check working directory | ProgramParametersConfigurator
        val workingDir = pathMacroManager.expandPath(workingDirectory)
        exists = try {
            Files.exists(Paths.get(workingDir))
        } catch (e: InvalidPathException) {
            false
        }
        if (!exists) {
            throw RuntimeConfigurationWarning(ExecutionBundle.message("dialog.message.working.directory.doesn.t.exist", workingDir))
        }

        // Check Module and SDK
        val module = ModuleManager.getInstance(project).findModuleByName(moduleName)
        if (module == null) {
            throw RuntimeConfigurationException(OCamlBundle.message("ocaml.runConfigurationType.module.not.found"))
        } else if (OCamlSdkIDEUtils.getModuleSdk(module) == null) {
            throw RuntimeConfigurationException(OCamlBundle.message("ocaml.runConfigurationType.module.sdk.not.set"))
        }
    }

    override fun getConfigurationEditor() = DuneRunConfigurationEditor(project)

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        val child = element.getOrCreateChild(DUNE_KEY)
        child.setAttribute(FILENAME, duneFile)
        child.setAttribute(TARGET, target)
        child.setAttribute(WORKING_DIRECTORY, workingDirectory)
        child.setAttribute(ARGUMENTS, arguments)
        child.setAttribute(MODULE_NAME, moduleName)
        environmentVariables.writeExternal(child)
    }

    override fun readExternal(element: Element) {
        super.readExternal(element)
        val child = element.getChild(DUNE_KEY)
        if (child != null) {
            duneFile = child.getAttributeValue(FILENAME) ?: ""
            target = child.getAttributeValue(TARGET) ?: ""
            workingDirectory = child.getAttributeValue(WORKING_DIRECTORY) ?: ""
            arguments = child.getAttributeValue(ARGUMENTS) ?: ""
            moduleName = child.getAttributeValue(MODULE_NAME) ?: ""
            environmentVariables = EnvironmentVariablesData.readExternal(child)
        }
    }

    override fun getState(executor: Executor, executionEnvironment: ExecutionEnvironment): RunProfileState {//
        return object : CommandLineState(executionEnvironment) {
            override fun startProcess(): ProcessHandler {
                // Locate the dune file
                val duneFilePath = PathMacroManager.getInstance(project).expandPath(duneFile)
                val duneFolder = File(duneFilePath).parentFile.toPath().toAbsolutePath().toString()

                // Locate the module and the sdk
                val module = ModuleManager.getInstance(project).findModuleByName(moduleName)
                    ?: error("Error: Module '$moduleName' was not found.")
                val sdk = OCamlSdkIDEUtils.getModuleSdk(module)
                    ?: error("Error: Module SDK for '$moduleName' was not found.")

                // Compile arguments
                val parentEnvironment = when {
                    environmentVariables.isPassParentEnvs -> EnvironmentUtil.getEnvironmentMap()
                    else -> emptyMap()
                }
                val env = (parentEnvironment + environmentVariables.envs).toMutableMap()

                // Invoke command
                val cmd = OCamlSdkProvidersManager.getDuneExecCommand(sdk.homePath!!, duneFolder, target, env) ?: error("Your SDK is not supported (${sdk.homePath}).")
                val processHandler = ColoredProcessHandler(cmd)
                processHandler.setShouldKillProcessSoftly(true)
                ProcessTerminatedListener.attach(processHandler)
                return processHandler
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
    override fun getDisplayName(): String = DuneBundle.message("run.configuration.name")
    override fun getConfigurationTypeDescription(): String = DuneBundle.message("run.configuration.description")
    override fun getIcon() = DuneIcons.Nodes.DUNE
    override fun getId() = "DUNE_TARGET_RUN_CONFIGURATION"
    override fun getConfigurationFactories() = arrayOf(DuneRunConfigurationFactory(this))

    @Suppress("CompanionObjectInExtension")
    companion object {
        @JvmStatic
        val instance: DuneRunConfigurationType
            get() = findConfigurationType(DuneRunConfigurationType::class.java)
    }
}

class DuneRunConfigurationEditor(project: Project) : SettingsEditor<DuneRunConfiguration>() {
    private val filenameField = TextFieldWithBrowseButton()
    private val targetField = EditorTextField("")
    private val argumentsField = ExpandableTextField()
    private val workingDirectoryField = TextFieldWithBrowseButton()
    private val environmentVarsComponent = EnvironmentVariablesComponent()

    private val panel by lazy {
        FormBuilder.createFormBuilder()
            .setAlignLabelOnRight(false)
            .setHorizontalGap(UIUtil.DEFAULT_HGAP)
            .setVerticalGap(UIUtil.DEFAULT_VGAP)
            .addLabeledComponent(DuneBundle.message("run.configuration.editor.filename.label"), filenameField)
            .addLabeledComponent(DuneBundle.message("run.configuration.editor.target.label"), targetField)
            .addComponent(LabeledComponent.create(argumentsField, DuneBundle.message("run.configuration.editor.arguments.label")))
            .addLabeledComponent(DuneBundle.message("run.configuration.editor.working.directory.label"), createComponentWithMacroBrowse(workingDirectoryField))
            .addComponent(environmentVarsComponent)
            .panel
    }

    init {
        filenameField.addBrowseFolderListener(
            DuneBundle.message("file.chooser.title"),
            DuneBundle.message("file.chooser.description"),
            project,
            DuneFileChooserDescriptor()
        )
        workingDirectoryField.addBrowseFolderListener(
            DuneBundle.message("working.directory.file.chooser"),
            DuneBundle.message("working.directory.file.chooser.description"),
            project,
            FileChooserDescriptorFactory.createSingleFolderDescriptor())
    }

    override fun createEditor(): JPanel = panel

    override fun applyEditorTo(configuration: DuneRunConfiguration) {
        configuration.duneFile = filenameField.text
        configuration.target = targetField.text
        configuration.workingDirectory = workingDirectoryField.text
        configuration.environmentVariables = environmentVarsComponent.envData
        configuration.arguments = argumentsField.text
    }

    override fun resetEditorFrom(configuration: DuneRunConfiguration) {
        filenameField.text = configuration.duneFile
        targetField.text = configuration.target
        workingDirectoryField.text = configuration.workingDirectory
        environmentVarsComponent.envData = configuration.environmentVariables
        argumentsField.text = configuration.arguments
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

class DuneFileChooserDescriptor : FileChooserDescriptor(true, false, false, false, false, false) {
    init {
        title = DuneBundle.message("file.chooser.title")
    }

    override fun isFileVisible(file: VirtualFile, showHiddenFiles: Boolean) = when {
        !showHiddenFiles && FileElement.isFileHidden(file) -> false
        file.isDirectory -> true
        else -> FileTypeRegistry.getInstance().isFileOfType(file, DuneFileType)
    }

    override fun isFileSelectable(file: VirtualFile?) =
        file != null && !file.isDirectory && isFileVisible(file, true)
}