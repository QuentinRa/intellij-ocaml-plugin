package com.ocaml.ide.module.wizard

import com.intellij.ide.projectWizard.NewProjectWizardConstants
import com.intellij.ide.wizard.AbstractNewProjectWizardMultiStep
import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.BuildSystemNewProjectWizardData
import com.intellij.ide.wizard.LanguageNewProjectWizardData.Companion.languageData
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.ide.wizard.language.LanguageGeneratorNewProjectWizard
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.project.Project
import com.ocaml.OCamlBundle.message
import com.ocaml.icons.OCamlIcons
import com.ocaml.ide.module.wizard.buildSystem.BuildSystemOCamlNewProjectWizard
import javax.swing.Icon

interface BuildSystemOCamlNewProjectWizardData : BuildSystemNewProjectWizardData

// For now, we can't build anything
// But, assume we have a "Makefile" build system, a "dune" build system, etc.
class OCamlDefaultBuiltSystemWizard : BuildSystemOCamlNewProjectWizard {
    override val name: String = message("project.wizard.build.system.none")
    override fun createStep(parent: OCamlNewProjectWizard.OCamlNewProjectWizardStep): NewProjectWizardStep = Step(parent)

    private class Step(parent: NewProjectWizardStep) : AbstractNewProjectWizardStep(parent) {
    }
}

class OCamlNewProjectWizard : LanguageGeneratorNewProjectWizard {
    override val icon: Icon get() = OCamlIcons.Nodes.OCAML_MODULE
    override val name: String = message("language.name")
    override val ordinal: Int = 200

    override fun createStep(parent: NewProjectWizardStep): NewProjectWizardStep = OCamlNewProjectWizardStep(parent)

    class OCamlNewProjectWizardStep(parent: NewProjectWizardStep) :
        AbstractNewProjectWizardMultiStep<OCamlNewProjectWizardStep, BuildSystemOCamlNewProjectWizard>(parent, BuildSystemOCamlNewProjectWizard.EP_NAME),
        BuildSystemOCamlNewProjectWizardData
    {
        override val label: String  get() = message("project.wizard.build.system")
        override val self: OCamlNewProjectWizardStep get() = this

        override fun setupProject(project: Project) {}

        override var buildSystem: String
            get() = step
            set(value) { step = value }
        override val buildSystemProperty: GraphProperty<String>
            get() = stepProperty

        @Deprecated("See LanguageNewProjectWizardData documentation for details")
        override var language: String
            get() = languageData!!.language
            set(value) { languageData!!.language = value }
        override val languageProperty: GraphProperty<String>
            get() = languageData!!.languageProperty

        override var name: String
            get() = languageData!!.name
            set(value) { languageData!!.name = value }
        override val nameProperty: GraphProperty<String>
            get() = languageData!!.nameProperty

        override var path: String
            get() = languageData!!.path
            set(value) { languageData!!.path = value }
        override val pathProperty: GraphProperty<String>
            get() = languageData!!.pathProperty
    }
}