package com.ocaml.ide.module.wizard.templates

import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.ocaml.utils.OCamlFileUtils

object OCamlTemplateProvider {
    val defaultInstructions: TemplateBuildInstructions
        get() = OCamlDefaultTemplateInstructions()

    /**
     * Default instructions.
     * - create src
     * - create src/hello_world.ml
     */
    private class OCamlDefaultTemplateInstructions : TemplateBuildInstructions {
        override fun createFiles(sourceRoot: VirtualFile?) {
            val sourceRootFile = VfsUtilCore.virtualToIoFile(sourceRoot!!)
            OCamlFileUtils.createFile(sourceRootFile, "hello_world.ml", "let _ = Format.printf \"Hello, World!\"")
        }
    }
}
