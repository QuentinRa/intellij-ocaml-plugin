package com.ocaml.ide.module.wizard

import com.intellij.ide.wizard.AbstractNewProjectWizardMultiStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.ide.wizard.language.LanguageGeneratorNewProjectWizard
import com.ocaml.OCamlBundle.message
import com.ocaml.icons.OCamlIcons
import com.ocaml.ide.module.wizard.buildSystem.BuildSystemOCamlNewProjectWizard
import javax.swing.Icon

/**
 * The OCaml Wizard as a few default fields due to LanguageGeneratorNewProjectWizard.
 * By using BuildSystemOCamlNewProjectWizard EP and registering "<newProjectWizard.ocaml.buildSystem ...>" in the XML,
 * We can add buildSystems (such as Gradle, Maven, IntelliJ for Java) and their options.
 */
class OCamlNewProjectWizard : LanguageGeneratorNewProjectWizard {
    override val icon: Icon get() = OCamlIcons.Nodes.OCAML_MODULE
    override val name: String = message("language.name")
    override val ordinal: Int = 200

    override fun createStep(parent: NewProjectWizardStep): NewProjectWizardStep = OCamlNewProjectWizardStep(parent)

    class OCamlNewProjectWizardStep(parent: NewProjectWizardStep) :
        AbstractNewProjectWizardMultiStep<OCamlNewProjectWizardStep, BuildSystemOCamlNewProjectWizard>(parent, BuildSystemOCamlNewProjectWizard.EP_NAME)
    {
        override val label: String  get() = message("project.wizard.build.system")
        override val self: OCamlNewProjectWizardStep get() = this
    }
}