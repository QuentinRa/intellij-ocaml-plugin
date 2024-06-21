package com.ocaml.sdk.runConfiguration

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.StoredProperty
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.util.ui.FormBuilder
import com.ocaml.OCamlBundle.message
import com.ocaml.icons.OCamlIcons
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JPanel


private const val OCAML_RUN_CONFIGURATION_ID = "OCAML_RUN_CONFIGURATION_TYPE"

// Implement "ConfigurationType" -OR- extend "ConfigurationTypeBase"?
class OCamlRunConfigurationType : ConfigurationType {
    override fun getDisplayName(): String = message("ocaml.runConfigurationType.name")
    override fun getConfigurationTypeDescription(): String = message("ocaml.runConfigurationType.description")
    override fun getIcon(): Icon = OCamlIcons.Nodes.OCAML_MODULE
    override fun getId(): String = OCAML_RUN_CONFIGURATION_ID
    override fun getConfigurationFactories(): Array<ConfigurationFactory> = arrayOf(OCamlConfigurationFactory(this))
}

class OCamlConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {
    override fun getId(): String = OCAML_RUN_CONFIGURATION_ID
    override fun getOptionsClass(): Class<out BaseState> = OCamlRunConfigurationOptions::class.java
    override fun createTemplateConfiguration(project: Project): RunConfiguration = OCamlRunConfiguration(project, this, "Demo")
}

class OCamlRunConfigurationOptions : RunConfigurationOptions() {
    private val myScriptName: StoredProperty<String?> = string("").provideDelegate(
        this, "scriptName"
    )

    var scriptName: String?
        get() = myScriptName.getValue(this)
        set(scriptName) {
            myScriptName.setValue(this, scriptName)
        }
}

class OCamlRunConfiguration(
    project: Project?,
    factory: ConfigurationFactory?,
    name: String?
) : RunConfigurationBase<OCamlRunConfigurationOptions?>(project!!, factory, name) {
    override fun getOptions(): OCamlRunConfigurationOptions {
        return super.getOptions() as OCamlRunConfigurationOptions
    }

    var scriptName: String?
        get() = options.scriptName
        set(scriptName) {
            options.scriptName = scriptName
        }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration?> = OCamlRunConfigurationSettingsEditor()

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? {
        return object : CommandLineState(environment) {
            override fun startProcess(): ProcessHandler {
                val commandLine = GeneralCommandLine(options.scriptName)
                val processHandler = ProcessHandlerFactory.getInstance()
                    .createColoredProcessHandler(commandLine)
                ProcessTerminatedListener.attach(processHandler)
                return processHandler
            }
        }
    }
}

class OCamlRunConfigurationSettingsEditor : SettingsEditor<OCamlRunConfiguration>() {
    private val myPanel: JPanel
    private val scriptPathField = TextFieldWithBrowseButton()

    init {
        scriptPathField.addBrowseFolderListener(
            "Select Script File", null, null,
            FileChooserDescriptorFactory.createSingleFileDescriptor()
        )
        myPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Script file", scriptPathField)
            .panel
    }

    override fun resetEditorFrom(demoRunConfiguration: OCamlRunConfiguration) {
        scriptPathField.text = demoRunConfiguration.scriptName ?: ""
    }

    override fun applyEditorTo(demoRunConfiguration: OCamlRunConfiguration) {
        demoRunConfiguration.scriptName = scriptPathField.text
    }

    override fun createEditor(): JComponent = myPanel
}