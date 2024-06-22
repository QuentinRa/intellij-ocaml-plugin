package com.ocaml.ide.files.actions

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.util.IncorrectOperationException
import com.ocaml.icons.OCamlIcons
import com.ocaml.ide.files.OCamlFileType
import com.ocaml.ide.files.OCamlInterfaceFileType
import com.ocaml.ide.module.OCamlIdeaModuleType

/**
 * Create a .ml
 * Create a .mli
 * Create both a .ml and a .mli
 */
class OCamlCreateFileAction :
    CreateFileFromTemplateAction(ACTION_NAME, "", OCamlIcons.FileTypes.OCAML_SOURCE_AND_INTERFACE) {

    override fun isAvailable(dataContext: DataContext): Boolean {
        if (!super.isAvailable(dataContext)) return false
        val project = CommonDataKeys.PROJECT.getData(dataContext) ?: return false
        // if we did click on a file
        val v = CommonDataKeys.PSI_FILE.getData(dataContext)
        if (v != null) { // is the file we clicked on
            val module = ModuleUtil.findModuleForFile(v)
            if (module != null) // inside an ocaml module?
                return ModuleType.get(module) is OCamlIdeaModuleType
        }
        // is there an ocaml module in the project?
        for (module in ModuleManager.getInstance(project).modules) {
            if (ModuleType.get(module) is OCamlIdeaModuleType) return true
        }
        return false
    }

    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        val ml = OCamlFileType.DOT_DEFAULT_EXTENSION
        val mli = OCamlInterfaceFileType.DOT_DEFAULT_EXTENSION
        val mlAndMli = "$ml + $mli"

        builder.setTitle(ACTION_NAME) // create .ml
            .addKind(ml, OCamlIcons.FileTypes.OCAML_SOURCE, OCAML_FILE_TEMPLATE) // create .mli
            .addKind(
                mli,
                OCamlIcons.FileTypes.OCAML_INTERFACE,
                OCAML_INTERFACE_TEMPLATE
            ) // create .ml + .mli
            .addKind(mlAndMli, OCamlIcons.FileTypes.OCAML_SOURCE_AND_INTERFACE, DUMMY_TEMPLATE)
    }

    override fun createFile(name: String, templateName: String, dir: PsiDirectory): PsiFile? {
        // try creating both .mli and .mli
        if (templateName == DUMMY_TEMPLATE) {
            val ml = OCamlFileType.DOT_DEFAULT_EXTENSION
            val mli = OCamlInterfaceFileType.DOT_DEFAULT_EXTENSION
            var file: PsiFile? = null
            var message: String? = ""
            // try creating .mli, if needed
            try {
                dir.checkCreateFile(name + mli)
                file = super.createFile(name, OCAML_INTERFACE_TEMPLATE, dir)
            } catch (e: IncorrectOperationException) {
                message = e.localizedMessage
            }
            // try creating .ml, if needed
            try {
                dir.checkCreateFile(name + ml)
                file = super.createFile(name, OCAML_FILE_TEMPLATE, dir)
            } catch (e: IncorrectOperationException) {
                message = e.localizedMessage
            }

            // return the last exception
            if (file == null) throw IncorrectOperationException(message)

            return file
        }
        return super.createFile(name, templateName, dir)
    }

    override fun getActionName(
        directory: PsiDirectory,
        newName: String,
        templateName: String
    ): @NlsContexts.Command String {
        return ACTION_NAME
    }

    companion object {
        private const val ACTION_NAME = "OCaml"

        // Templates
        private const val OCAML_FILE_TEMPLATE: String = "OCaml File"
        private const val OCAML_INTERFACE_TEMPLATE: String = "OCaml Interface"

        /*
         * ISSUE: we can define ONLY one behavior per template, but for that
         * we need a valid template. As I need a third template to create .ml + .mli,
         * I'm using this dummy template, then doing the job with valid templates in "createFile".
         */
        private const val DUMMY_TEMPLATE = "DummyTemplate"
    }
}
