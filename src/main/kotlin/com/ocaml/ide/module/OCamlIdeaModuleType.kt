package com.ocaml.ide.module

import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import com.ocaml.OCamlBundle.message
import com.ocaml.icons.OCamlIcons
import javax.swing.Icon

private const val OCamlIdeaModuleId = "OCAML_MODULE"

/**
 * Defines the name, description, and icon of the module.
 */
class OCamlIdeaModuleType : ModuleType<OCamlModuleBuilder>(OCamlIdeaModuleId) {
    override fun createModuleBuilder(): OCamlModuleBuilder = OCamlModuleBuilder()

    override fun getName() = message("ocaml.module")
    override fun getDescription(): String = message("ocaml.module.description")
    override fun getNodeIcon(isOpened: Boolean): Icon = OCamlIcons.Nodes.OCAML_MODULE

    companion object {
        val instance = ModuleTypeManager.getInstance().findByID(OCamlIdeaModuleId)
    }
}