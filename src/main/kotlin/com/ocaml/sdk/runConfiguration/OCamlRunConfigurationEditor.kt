// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.ocaml.sdk.runConfiguration

import com.intellij.application.options.ModuleDescriptionsComboBox
import com.intellij.execution.ui.CommonJavaParametersPanel
import com.intellij.execution.ui.ConfigurationModuleSelector
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.ui.EditorTextField
import com.intellij.ui.PanelWithAnchor
import com.intellij.util.ui.UIUtil
import javax.swing.JComponent
import javax.swing.JPanel

class OCamlRunConfigurationEditor(private val project: Project) : SettingsEditor<OCamlRunConfiguration>(), PanelWithAnchor {
    private var mainPanel: JPanel? = null
    private var mainClass: LabeledComponent<OCamlModuleEditorField>? = null

    private var commonProgramParameters: CommonJavaParametersPanel? = null
    private var moduleChooser: LabeledComponent<ModuleDescriptionsComboBox>? = null

    private var anchor: JComponent

    private val moduleSelector = ConfigurationModuleSelector(project, moduleChooser!!.component)

    init {
        commonProgramParameters!!.setModuleContext(moduleSelector.module)
        moduleChooser!!.component.addActionListener { commonProgramParameters!!.setModuleContext(moduleSelector.module) }
        anchor = UIUtil.mergeComponentsWithAnchor(
            mainClass, commonProgramParameters, moduleChooser
        )!!
    }

    override fun applyEditorTo(configuration: OCamlRunConfiguration) {
        commonProgramParameters!!.applyTo(configuration)
        moduleSelector.applyTo(configuration)

        configuration.runClass = mainClass!!.component.className
    }

    override fun resetEditorFrom(configuration: OCamlRunConfiguration) {
        commonProgramParameters!!.reset(configuration)
        moduleSelector.reset(configuration)
        val runClass = configuration.runClass
        mainClass!!.component.text = runClass?.replace("\\$".toRegex(), "\\.") ?: ""
    }

    override fun createEditor(): JComponent = mainPanel!!

    private fun createUIComponents() {
        mainClass = LabeledComponent()
        val field = OCamlModuleEditorField()
        mainClass!!.setComponent(field)
    }

    override fun getAnchor(): JComponent = anchor

    override fun setAnchor(anchor: JComponent?) {
        this.anchor = anchor!!
        mainClass!!.anchor = anchor
        commonProgramParameters!!.anchor = anchor
        moduleChooser!!.anchor = anchor
    }

    // ClassEditorField
    private class OCamlModuleEditorField : EditorTextField() {

        val className: String get() = text
    }
}