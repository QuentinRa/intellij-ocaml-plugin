package com.ocaml.ide.module.wizard.buildSystem

import com.dune.DuneBundle.message
import com.intellij.ide.wizard.NewProjectWizardChainStep.Companion.nextStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.ocaml.ide.module.wizard.OCamlNewProjectWizard
import com.ocaml.ide.module.wizard.OCamlNewProjectWizardAssetStep
import com.ocaml.ide.module.wizard.OCamlNewProjectWizardBaseStep

class OCamlDuneBuildSystemWizard : OCamlDefaultBuildSystemWizard() {
    override val name: String = NAME
    override fun createStep(parent: OCamlNewProjectWizard.OCamlNewProjectWizardStep): NewProjectWizardStep =
        Step(parent).nextStep(::OCamlNewProjectWizardAssetStep)

    private class Step(parent: OCamlNewProjectWizard.OCamlNewProjectWizardStep) : OCamlNewProjectWizardBaseStep(parent)

    companion object {
        val NAME = message("project.wizard.build.system.dune")
    }
}