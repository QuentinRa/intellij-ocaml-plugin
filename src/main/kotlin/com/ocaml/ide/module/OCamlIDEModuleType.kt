package com.ocaml.ide.module

import com.intellij.openapi.module.ModuleType
import com.ocaml.OCamlBundle.message
import com.ocaml.icons.OCamlIcons
import org.jetbrains.annotations.Nls
import javax.swing.Icon

/**
 * Defines the name, description, and icon of the module.
 */
object OCamlIDEModuleType : ModuleType<OCamlModuleBuilder>("OCAML_MODULE") {
    //
    // Builder
    //
    override fun createModuleBuilder(): OCamlModuleBuilder {
        return OCamlModuleBuilder()
    }

    //
    // Name, description, icon
    //
    override fun getName(): @Nls(capitalization = Nls.Capitalization.Title) String {
        return message("ocaml.module")
    }

    override fun getDescription(): @Nls(capitalization = Nls.Capitalization.Sentence) String {
        return message("ocaml.module.description")
    }

    override fun getNodeIcon(isOpened: Boolean): Icon {
        return OCamlIcons.Nodes.OCAML_MODULE
    }
}