package com.ocaml.utils.adaptor.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ui.configuration.JdkComboBox
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel
import com.intellij.openapi.util.Condition
import com.intellij.util.Consumer

/**
 * @see JdkComboBox
 */
class JdkComboBoxAdaptor(
    project: Project?,
    sdkModel: ProjectSdksModel,
    sdkTypeFilter: Condition<in SdkTypeId>,
    sdkFilter: Condition<in Sdk?>?,
    creationFilter: Condition<in SdkTypeId?>?,
    onNewSdkAdded: Consumer<in Sdk?>?
) : JdkComboBox(project, sdkModel, sdkTypeFilter, sdkFilter, creationFilter, onNewSdkAdded) {

    override fun isProjectJdkSelected(): Boolean {
        return selectedItem is ProjectJdkComboBoxItem
    }

}