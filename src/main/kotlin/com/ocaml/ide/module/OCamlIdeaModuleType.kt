package com.ocaml.ide.module

import com.intellij.openapi.module.ModuleType
import com.ocaml.OCamlBundle.message
import com.ocaml.icons.OCamlIcons
import javax.swing.Icon

/**
 * Defines the name, description, and icon of the module.
 */
object OCamlIdeaModuleType : ModuleType<OCamlModuleBuilder>("OCAML_MODULE") {
    override fun createModuleBuilder(): OCamlModuleBuilder = OCamlModuleBuilder()

    override fun getName() = message("ocaml.module")
    override fun getDescription(): String = message("ocaml.module.description")
    override fun getNodeIcon(isOpened: Boolean): Icon = OCamlIcons.Nodes.OCAML_MODULE
}