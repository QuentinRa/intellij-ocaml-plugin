package com.ocaml.ide.module.wizard.buildSystem

import com.dune.DuneBundle.message
import com.intellij.ide.wizard.NewProjectWizardStep
import com.ocaml.ide.module.wizard.OCamlNewProjectWizard
import com.ocaml.ide.module.wizard.OCamlNewProjectWizardBaseStep

class OCamlDuneBuildSystemWizard : OCamlDefaultBuildSystemWizard() {
    override val name: String = message("project.wizard.build.system.dune")
    override fun createStep(parent: OCamlNewProjectWizard.OCamlNewProjectWizardStep): NewProjectWizardStep = Step(parent)

    private class Step(parent: OCamlNewProjectWizard.OCamlNewProjectWizardStep) : OCamlNewProjectWizardBaseStep(parent) {
    }
}