package com.ocaml.sdk.runConfiguration

import com.intellij.execution.application.JvmMainMethodRunConfigurationOptions
import com.intellij.execution.configurations.*
import com.intellij.execution.configurations.ConfigurationTypeUtil.findConfigurationType
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project
import com.ocaml.OCamlBundle.message
import com.ocaml.icons.OCamlIcons
import javax.swing.Icon

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
        val instance: OCamlRunConfigurationType
            get() = findConfigurationType(OCamlRunConfigurationType::class.java)
    }
}

internal class OCamlRunConfigurationModule(project: Project) : RunConfigurationModule(project) {}

// ApplicationConfigurationType.java | Anonymous class
internal class OCamlConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {
    override fun getId(): String = OCAML_RUN_CONFIGURATION_ID
    override fun getOptionsClass(): Class<out BaseState> = JvmMainMethodRunConfigurationOptions::class.java
    override fun createTemplateConfiguration(project: Project): RunConfiguration =
        OCamlRunConfiguration("", OCamlRunConfigurationModule(project), this)
    override fun isEditableInDumbMode(): Boolean = true
}