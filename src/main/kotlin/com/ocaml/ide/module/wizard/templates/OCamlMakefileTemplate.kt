package com.ocaml.ide.module.wizard.templates

import com.intellij.ide.util.projectWizard.AbstractModuleBuilder
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.ProjectTemplate
import com.ocaml.OCamlBundle.message
import com.ocaml.icons.OCamlIcons
import com.ocaml.utils.OCamlFileUtils
import javax.swing.Icon

/**
 * Allow the creation of a Makefile project, which includes the following
 * - src
 * - src/hello_world.ml
 * - src/hello_world.mli
 * - src/test_hello_world.ml
 * - Makefile
 */
internal class OCamlMakefileTemplate : ProjectTemplate, TemplateBuildInstructions {
    override fun getName(): String = message("template.makefile.title")
    override fun getDescription(): String = message("template.makefile.description")
    override fun getIcon(): Icon = OCamlIcons.External.MAKEFILE
    override fun createModuleBuilder(): AbstractModuleBuilder =
        throw UnsupportedOperationException("OCamlMakefileTemplate#createModuleBuilder should not be called")

    @Deprecated("Deprecated in Java", ReplaceWith("null"))
    override fun validateSettings(): ValidationInfo? = null

    override fun createFiles(sourceRoot: VirtualFile?) {
        val sourceFolder = VfsUtilCore.virtualToIoFile(sourceRoot!!)
        val makefileContent: String = OCamlFileUtils.loadFileContent("/templates/Makefile/Makefile")
        OCamlFileUtils.createFile(sourceFolder, "hello_world.mli", "val hello_world : unit -> unit")
        OCamlFileUtils.createFile(sourceFolder, "hello_world.ml", "let hello_world () = Format.printf \"Hello, World!@.\"")
        OCamlFileUtils.createFile(sourceFolder, "test_hello_world.ml", "open Hello_world\n\nlet _ = hello_world ()")
        OCamlFileUtils.createFile(sourceFolder.parentFile, "Makefile", makefileContent)
    }
}
