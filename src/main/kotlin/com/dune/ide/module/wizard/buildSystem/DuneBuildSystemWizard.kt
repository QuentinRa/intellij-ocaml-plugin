package com.dune.ide.module.wizard.buildSystem

import com.dune.DuneBundle.message
import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.ocaml.ide.module.wizard.OCamlNewProjectWizard
import com.ocaml.ide.module.wizard.buildSystem.OCamlDefaultBuildSystemWizard

@Deprecated("Not used yet.")
class DuneBuildSystemWizard : OCamlDefaultBuildSystemWizard() {
    override val name: String = message("project.wizard.build.system.dune")
    override fun createStep(parent: OCamlNewProjectWizard.OCamlNewProjectWizardStep): NewProjectWizardStep = Step(parent)

    private class Step(parent: NewProjectWizardStep) : AbstractNewProjectWizardStep(parent) {
    }
}