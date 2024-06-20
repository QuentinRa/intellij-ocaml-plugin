package com.ocaml.ide.module.wizard

import com.intellij.ide.wizard.AbstractNewProjectWizardStep
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
import com.ocaml.sdk.OCamlSdkType
import com.ocaml.utils.adaptor.ui.JdkComboBoxAdaptor

// com.intellij.ide.projectWizard.generators.IntelliJNewProjectWizardStep
open class OCamlNewProjectWizardBaseStep(parent: OCamlNewProjectWizard.OCamlNewProjectWizardStep) : AbstractNewProjectWizardStep(parent) {
    private val myProject: Project = parent.context.project ?: ProjectManager.getInstance().defaultProject
    private val mySdksModel: ProjectSdksModel = ProjectSdksModel() // project SDK, add/create SDKs, ...

    init {
        mySdksModel.reset(myProject)
    }

    override fun setupUI(builder: Panel) {
        super.setupUI(builder)

        val sdkTypeFilter: Condition<in SdkTypeId> = Condition<SdkTypeId> { sdk -> sdk is OCamlSdkType }
        builder.row(message("project.wizard.ocaml.sdk")) {
            val combo = JdkComboBoxAdaptor(myProject, mySdksModel, sdkTypeFilter, null, null, null)
            if (!context.isCreatingNewProject) combo.showProjectSdkItem()
            cell(combo)
                .columns(COLUMNS_LARGE)
        }.bottomGap(BottomGap.SMALL)
    }
}