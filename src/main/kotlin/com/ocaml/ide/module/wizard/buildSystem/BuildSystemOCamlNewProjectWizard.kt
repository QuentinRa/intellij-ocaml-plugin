package com.ocaml.ide.module.wizard.buildSystem

import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.NewProjectWizardMultiStepFactory
import com.intellij.openapi.extensions.ExtensionPointName
import com.ocaml.ide.module.wizard.OCamlNewProjectWizard

interface BuildSystemOCamlNewProjectWizard : NewProjectWizardMultiStepFactory<OCamlNewProjectWizard.OCamlNewProjectWizardStep> {
    override fun isEnabled(context: WizardContext): Boolean = true

    companion object {
        val EP_NAME = ExtensionPointName<BuildSystemOCamlNewProjectWizard>("com.intellij.newProjectWizard.ocaml.buildSystem")
    }
}