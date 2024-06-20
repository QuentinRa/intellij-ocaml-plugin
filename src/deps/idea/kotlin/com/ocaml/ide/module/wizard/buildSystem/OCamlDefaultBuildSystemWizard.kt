package com.ocaml.ide.module.wizard.buildSystem

import com.intellij.ide.wizard.NewProjectWizardStep
import com.ocaml.OCamlBundle.message
import com.ocaml.ide.module.wizard.OCamlNewProjectWizard
import com.ocaml.ide.module.wizard.OCamlNewProjectWizardBaseStep

open class OCamlDefaultBuildSystemWizard : BuildSystemOCamlNewProjectWizard {
    override val name: String = message("project.wizard.build.system.none")
    override fun createStep(parent: OCamlNewProjectWizard.OCamlNewProjectWizardStep): NewProjectWizardStep = Step(parent)

    private class Step(parent: OCamlNewProjectWizard.OCamlNewProjectWizardStep) : OCamlNewProjectWizardBaseStep(parent) {
    }
}