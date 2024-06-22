package com.ocaml.ide.module.wizard.templates

import com.intellij.ide.util.projectWizard.AbstractModuleBuilder
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.ProjectTemplate
import com.ocaml.OCamlBundle.message
import com.ocaml.icons.OCamlIcons
import com.ocaml.sdk.providers.OCamlSdkProvidersManager
import com.ocaml.utils.OCamlFileSystemUtils
import javax.swing.Icon

/**
 * Allow the creation of a dune project, which includes the following
 * - src
 * - src/hello_world.ml
 * - src/hello_world.mli
 * - src/test_hello_world.ml
 * - src/dune
 * - dune-project
 */
internal class OCamlDuneTemplate : ProjectTemplate, TemplateBuildInstructions {
    override fun getName(): String = message("template.dune.title")
    override fun getDescription(): String = message("template.dune.description")
    override fun getIcon(): Icon = OCamlIcons.External.DUNE
    override fun createModuleBuilder(): AbstractModuleBuilder =
        throw UnsupportedOperationException("OCamlDuneTemplate#createModuleBuilder should not be called")

    @Deprecated("Deprecated in Java", ReplaceWith("null"))
    override fun validateSettings(): ValidationInfo? = null

    override fun createFiles(sourceRoot: VirtualFile?) = Unit
    override fun createFiles(sourceRoot: VirtualFile?, sdkHomePath: String?) {
        val sourceFolder = VfsUtilCore.virtualToIoFile(sourceRoot!!)
        val rootFolder = sourceFolder.parentFile
        val version = OCamlSdkProvidersManager.getDuneVersion(sdkHomePath)

        // OCaml Source Files
        OCamlFileSystemUtils.createFile(sourceFolder, "hello_world.mli", "val hello_world : unit -> unit")
        OCamlFileSystemUtils.createFile(sourceFolder, "hello_world.ml", "let hello_world () = Format.printf \"Hello, World!@.\"")
        OCamlFileSystemUtils.createFile(sourceFolder, "test_hello_world.ml", "open Hello_world\n\nlet _ = hello_world ()")

        // Dune Source Files
        OCamlFileSystemUtils.createFile(sourceFolder, "dune", "(executable\n (name test_hello_world))")
        OCamlFileSystemUtils.createFile(rootFolder, "dune-project", "(lang dune $version)")
    }
}