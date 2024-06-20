package com.ocaml.ide.module.wizard

import com.intellij.ide.wizard.AbstractNewProjectWizardMultiStep
import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.LanguageNewProjectWizardData.Companion.languageData
import com.intellij.ide.wizard.NewProjectWizardBaseData
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.ide.wizard.language.LanguageGeneratorNewProjectWizard
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel
import com.intellij.openapi.util.Condition
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.COLUMNS_LARGE
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.columns
import com.ocaml.OCamlBundle.message
import com.ocaml.icons.OCamlIcons
import com.ocaml.ide.module.wizard.buildSystem.BuildSystemOCamlNewProjectWizard
import com.ocaml.utils.adaptor.ui.JdkComboBoxAdaptor
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
        AbstractNewProjectWizardMultiStep<OCamlNewProjectWizardStep, BuildSystemOCamlNewProjectWizard>(parent, BuildSystemOCamlNewProjectWizard.EP_NAME),
            NewProjectWizardBaseData
    {
        override val label: String  get() = message("project.wizard.build.system")
        override val self: OCamlNewProjectWizardStep get() = this
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