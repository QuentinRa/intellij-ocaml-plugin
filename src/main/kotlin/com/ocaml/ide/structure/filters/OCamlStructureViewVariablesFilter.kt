package com.ocaml.ide.structure.filters

import com.intellij.ide.util.treeView.smartTree.ActionPresentation
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData
import com.intellij.ide.util.treeView.smartTree.Filter
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.ocaml.OCamlBundle
import com.ocaml.icons.OCamlIcons
import com.ocaml.ide.structure.OCamlStructureViewElement
import com.ocaml.language.psi.api.OCamlLetDeclaration

object OCamlStructureViewVariablesFilter : Filter {
    const val FILTER_ID = "SHOW_VARIABLES"

    override fun isVisible(treeNode: TreeElement): Boolean {
        return isVisible(treeNode as OCamlStructureViewElement)
    }

    private fun isVisible(treeNode: OCamlStructureViewElement): Boolean {
        val element = treeNode.value
        return if (element is OCamlLetDeclaration) !element.isVariable() else true
    }

    override fun getPresentation(): ActionPresentation {
        return ActionPresentationData(
            OCamlBundle.message("action.structureview.show.variables"), null,
            OCamlIcons.Nodes.VARIABLE
        )
    }

    override fun getName(): String = FILTER_ID
    override fun isReverted(): Boolean = true
}
