package com.ocaml.ide.structure.filters

import com.intellij.ide.util.treeView.smartTree.ActionPresentation
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData
import com.intellij.psi.PsiElement
import com.ocaml.OCamlBundle
import com.ocaml.icons.OCamlIcons
import com.ocaml.language.psi.OCamlTypedef

object OCamlStructureViewTypesFilter : OCamlStructureViewBaseFilter() {
    private const val FILTER_ID = "SHOW_TYPES"

    override fun isVisible(element: PsiElement): Boolean {
        return element !is OCamlTypedef
    }

    override fun getPresentation(): ActionPresentation {
        return ActionPresentationData(
            OCamlBundle.message("action.structureview.show.types"), null,
            OCamlIcons.Nodes.TYPE
        )
    }

    override fun getName(): String = FILTER_ID
}