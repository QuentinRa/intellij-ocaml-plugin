package com.ocaml.ide.structure.filters

import com.intellij.icons.AllIcons
import com.intellij.ide.util.treeView.smartTree.ActionPresentation
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.ocaml.OCamlBundle
import com.ocaml.ide.structure.OCamlStructureViewElement

object OCamlStructureViewNestedFilter : OCamlStructureViewBaseFilter() {
    private const val FILTER_ID = "NESTED_ELEMENTS"

    override fun isVisible(treeNode: TreeElement): Boolean {
        treeNode as OCamlStructureViewElement
        return treeNode.depth < 2
    }

    override fun getPresentation(): ActionPresentation {
        return ActionPresentationData(
            OCamlBundle.message("action.structureview.show.nested"), null,
            AllIcons.General.InspectionsEye
        )
    }

    override fun getName(): String = FILTER_ID

    override fun isReverted(): Boolean = true
}