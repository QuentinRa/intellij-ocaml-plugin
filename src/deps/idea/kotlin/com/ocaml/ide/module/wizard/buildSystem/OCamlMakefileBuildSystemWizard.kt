package com.ocaml.ide.module.wizard.buildSystem

import com.intellij.ide.wizard.NewProjectWizardChainStep.Companion.nextStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.ocaml.OCamlBundle.message
import com.ocaml.ide.module.wizard.OCamlNewProjectWizard
import com.ocaml.ide.module.wizard.OCamlNewProjectWizardAssetStep
import com.ocaml.ide.module.wizard.OCamlNewProjectWizardBaseStep

open class OCamlMakefileBuildSystemWizard : BuildSystemOCamlNewProjectWizard {
    override val name: String = NAME
    override fun createStep(parent: OCamlNewProjectWizard.OCamlNewProjectWizardStep): NewProjectWizardStep =
        Step(parent).nextStep(::OCamlNewProjectWizardAssetStep)

    private class Step(parent: OCamlNewProjectWizard.OCamlNewProjectWizardStep) : OCamlNewProjectWizardBaseStep(parent)

    companion object {
        val NAME = message("project.wizard.build.system.makefile")
    }
}