package com.ocaml.ide.module.wizard.templates

import com.intellij.ide.util.projectWizard.AbstractModuleBuilder
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.ProjectTemplate
import com.ocaml.OCamlBundle.message
import com.ocaml.icons.OCamlIcons
import com.ocaml.utils.OCamlFileSystemUtils
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
        val makefileContent: String = OCamlFileSystemUtils.loadFileContent("/templates/Makefile/Makefile")
        OCamlFileSystemUtils.createFile(sourceFolder, "hello_world.mli", "val hello_world : unit -> unit")
        OCamlFileSystemUtils.createFile(sourceFolder, "hello_world.ml", "let hello_world () = Format.printf \"Hello, World!@.\"")
        OCamlFileSystemUtils.createFile(sourceFolder, "test_hello_world.ml", "open Hello_world\n\nlet _ = hello_world ()")
        OCamlFileSystemUtils.createFile(sourceFolder.parentFile, "Makefile", makefileContent)
    }
}
