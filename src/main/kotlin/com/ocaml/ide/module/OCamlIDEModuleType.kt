package com.ocaml.ide.module

import com.intellij.openapi.module.ModuleType
import com.ocaml.OCamlBundle.message
import com.ocaml.icons.OCamlIcons
import javax.swing.Icon

/**
 * Defines the name, description, and icon of the module.
 */
// fixme: it's not based
@Deprecated("Not the correct type.")
object OCamlIDEModuleType : ModuleType<BaseOCamlModuleBuilder>("OCAML_MODULE") {
    override fun createModuleBuilder(): BaseOCamlModuleBuilder = BaseOCamlModuleBuilder()

    override fun getName() = message("ocaml.module")
    override fun getDescription(): String = message("ocaml.module.description")
    override fun getNodeIcon(isOpened: Boolean): Icon = OCamlIcons.Nodes.OCAML_MODULE
}