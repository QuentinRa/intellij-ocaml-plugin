package com.ocaml.ide.structure.filters

import com.intellij.ide.util.treeView.smartTree.ActionPresentation
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData
import com.intellij.psi.PsiElement
import com.ocaml.OCamlBundle
import com.ocaml.icons.OCamlIcons
import com.ocaml.language.psi.api.OCamlLetDeclaration

object OCamlStructureViewFunctionsFilter : OCamlStructureViewBaseFilter() {
    private const val FILTER_ID = "SHOW_FUNCTIONS"

    override fun isVisible(element: PsiElement): Boolean {
        return if (element is OCamlLetDeclaration) !element.isFunction() else true
    }

    override fun getPresentation(): ActionPresentation {
        return ActionPresentationData(
            OCamlBundle.message("action.structureview.show.functions"), null,
            OCamlIcons.Nodes.FUNCTION
        )
    }

    override fun getName(): String = FILTER_ID
}
