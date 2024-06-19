package com.ocaml.ide.module.wizard.templates

import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.ProjectTemplate
import com.ocaml.utils.OCamlFileUtils

object OCamlTemplateProvider {
    val availableTemplates: ArrayList<ProjectTemplate>
        /**
         * Returns the templates available for OCaml.
         * This may not be the way to do, but there is no indications for now,
         * so I will use this.
         */
        get() {
            val availableTemplates = ArrayList<ProjectTemplate>()
            availableTemplates.add(OCamlDuneTemplate())
            availableTemplates.add(OCamlMakefileTemplate())
            return availableTemplates
        }

    val defaultInstructions: TemplateBuildInstructions
        get() = OCamlDefaultTemplateInstructions()

    /**
     * Default instructions.
     * - create src
     * - create src/hello_world.ml
     */
    private class OCamlDefaultTemplateInstructions : TemplateBuildInstructions {
        override fun createFiles(rootModel: ModifiableRootModel?, sourceRoot: VirtualFile?) {
            val sourceRootFile = VfsUtilCore.virtualToIoFile(sourceRoot!!)
            OCamlFileUtils.createFile(sourceRootFile, "hello_world.ml", "let _ = Format.printf \"Hello, World!\"")
        }
    }
}
