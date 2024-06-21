package com.ocaml.sdk.runConfiguration

import com.intellij.diagnostic.logging.LogConfigurationPanel
import com.intellij.execution.ExecutionBundle
import com.intellij.execution.Executor
import com.intellij.execution.JavaRunConfigurationExtensionManager
import com.intellij.execution.application.ApplicationConfigurable
import com.intellij.execution.application.ApplicationConfiguration
import com.intellij.execution.application.JavaApplicationSettingsEditor
import com.intellij.execution.configurations.*
import com.intellij.execution.configurations.ConfigurationTypeUtil.findConfigurationType
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.options.SettingsEditorGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.registry.Registry
import com.intellij.util.ui.FormBuilder
import com.ocaml.OCamlBundle.message
import com.ocaml.icons.OCamlIcons
import org.jdom.Element
import java.util.*
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JPanel


private const val OCAML_RUN_CONFIGURATION_ID = "OCAML_RUN_CONFIGURATION_TYPE"

// Implement "ConfigurationType" -OR- extend "ConfigurationTypeBase"?
// Refer to "ApplicationConfigurationType.java"
class OCamlRunConfigurationType : ConfigurationType {
    override fun getDisplayName(): String = message("ocaml.runConfigurationType.name")
    override fun getConfigurationTypeDescription(): String = message("ocaml.runConfigurationType.description")
    override fun getIcon(): Icon = OCamlIcons.Nodes.OCAML_MODULE
    override fun getId(): String = OCAML_RUN_CONFIGURATION_ID
    override fun getConfigurationFactories(): Array<ConfigurationFactory> = arrayOf(OCamlConfigurationFactory(this))
    override fun isDumbAware(): Boolean = true

    companion object {
        fun getInstance(): OCamlRunConfigurationType {
            return findConfigurationType(OCamlRunConfigurationType::class.java)
        }
    }
}

// ApplicationConfigurationType.java | Anonymous class
private class OCamlConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {
    override fun getId(): String = OCAML_RUN_CONFIGURATION_ID
    override fun getOptionsClass(): Class<out BaseState> = OCamlRunConfigurationOptions::class.java
    override fun createTemplateConfiguration(project: Project): RunConfiguration = OCamlRunConfiguration("", project, type)
    override fun isEditableInDumbMode(): Boolean = true
}

// JvmMainMethodRunConfigurationOptions
private class OCamlRunConfigurationOptions : ModuleBasedConfigurationOptions()

private class OCamlRunConfigurationModule(project: Project) : RunConfigurationModule(project) {

}

// ApplicationConfiguration
private class OCamlRunConfiguration(
    name: String?,
    project: Project,
    factory: ConfigurationType
) : ModuleBasedConfiguration<OCamlRunConfigurationModule, Element>(name, OCamlRunConfigurationModule(project), factory.configurationFactories[0]) {
    override fun getOptions(): OCamlRunConfigurationOptions {
        return super.getOptions() as OCamlRunConfigurationOptions
    }

    override fun getValidModules(): MutableCollection<Module> {
        return ModuleManager.getInstance(project).modules.toMutableList()
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration?> {
        error("Not coded yet.")
        // new layout
//        println("Was ${Registry.`is`("ide.new.run.config", true)}")
//        if (Registry.`is`("ide.new.run.config", true)) {
//            return JavaApplicationSettingsEditor(this)
//        }
//        val group = SettingsEditorGroup<ApplicationConfiguration>()
//        group.addEditor(
//            ExecutionBundle.message("run.configuration.configuration.tab.title"), ApplicationConfigurable(
//                project
//            )
//        )
//        JavaRunConfigurationExtensionManager.instance.appendEditors(this, group)
//        group.addEditor(ExecutionBundle.message("logs.tab.title"), LogConfigurationPanel())
//        return group
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? {
        return object : CommandLineState(environment) {
            override fun startProcess(): ProcessHandler {
                error("Not implemented yet.")
            }
        }
    }
}

private class OCamlRunConfigurationSettingsEditor : SettingsEditor<OCamlRunConfiguration>() {
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
    }

    override fun applyEditorTo(demoRunConfiguration: OCamlRunConfiguration) {
    }

    override fun createEditor(): JComponent = myPanel
}