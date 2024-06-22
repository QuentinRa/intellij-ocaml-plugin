package com.ocaml.sdk.validator

import com.intellij.codeInsight.daemon.ProjectSdkSetupValidator
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ui.configuration.SdkPopupFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.ocaml.OCamlBundle
import com.ocaml.ide.files.utils.OCamlFileUtils
import com.ocaml.sdk.OCamlSdkType
import com.ocaml.sdk.utils.OCamlSdkIDEUtils

/**
 * If the user open a file (.ml/.mli => FileHelper.isOCaml) and the SDK isn't set,
 * then he/she will see a message to set up the SDK, along with a button to fix this error.
 */
class OCamlSDKValidator : ProjectSdkSetupValidator {
    override fun isApplicableFor(project: Project, file: VirtualFile): Boolean {
        // check that the file is in the project
        return OCamlFileUtils.isOCaml(file) && OCamlSdkIDEUtils.isInProject(project, file)
    }

    override fun getErrorMessage(project: Project, file: VirtualFile): String? {
        // checking module SDK
        val sdk = OCamlSdkIDEUtils.getModuleSdkForFile(project, file)
        if (sdk != null) return null // we got one, no error

        // error, not OCaml SDK
        return CONFIGURE_OCAMLC_SDK
    }

    override fun getFixHandler(
        project: Project,
        file: VirtualFile
    ): EditorNotificationPanel.ActionHandler {
        return SdkPopupFactory.newBuilder().withProject(project)
            .withSdkType(OCamlSdkType.instance!!) // module SDK or project SDK
            .updateSdkForFile(file)
            .buildEditorNotificationPanelHandler()
    }

    companion object {
        val CONFIGURE_OCAMLC_SDK: String = OCamlBundle.message("sdk.ask.configure")
    }
}