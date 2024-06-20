package com.ocaml.ide.module.wizard.buildSystem

import com.intellij.ide.JavaUiBundle
import com.intellij.ide.projectWizard.projectWizardJdkComboBox
import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ui.configuration.projectRoot.SdkDownloadTask
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.Panel
import com.ocaml.OCamlBundle.message
import com.ocaml.ide.module.wizard.OCamlNewProjectWizard

@Deprecated("Not used yet.")
open class OCamlDefaultBuildSystemWizard : BuildSystemOCamlNewProjectWizard {
    override val name: String = message("project.wizard.build.system.none")
    override fun createStep(parent: OCamlNewProjectWizard.OCamlNewProjectWizardStep): NewProjectWizardStep = Step(parent)

    // com.intellij.ide.projectWizard.generators.IntelliJNewProjectWizardStep
    private class Step(parent: OCamlNewProjectWizard.OCamlNewProjectWizardStep) : AbstractNewProjectWizardStep(parent) {
        val sdkProperty = propertyGraph.property<Sdk?>(null)
        val sdkDownloadTaskProperty = propertyGraph.property<SdkDownloadTask?>(null)

        override fun setupUI(builder: Panel) {
            super.setupUI(builder)
            builder.row(JavaUiBundle.message("label.project.wizard.new.project.jdk")) {
                projectWizardJdkComboBox(context, sdkProperty, sdkDownloadTaskProperty, "Test", context.projectJdk)
            }.bottomGap(BottomGap.SMALL)
        }
    }
}