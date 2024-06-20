package com.ocaml.ide.module.wizard.buildSystem

import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.ocaml.OCamlBundle.message
import com.ocaml.ide.module.wizard.OCamlNewProjectWizard

@Deprecated("Not used yet.")
open class OCamlMakefileBuildSystemWizard : BuildSystemOCamlNewProjectWizard {
    override val name: String = message("project.wizard.build.system.makefile")
    override fun createStep(parent: OCamlNewProjectWizard.OCamlNewProjectWizardStep): NewProjectWizardStep = Step(parent)

    private class Step(parent: NewProjectWizardStep) : AbstractNewProjectWizardStep(parent) {
    }
}