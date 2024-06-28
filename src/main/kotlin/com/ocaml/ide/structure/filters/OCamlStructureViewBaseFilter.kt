package com.ocaml.ide.structure.filters

import com.intellij.ide.util.treeView.smartTree.Filter
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.psi.PsiElement
import com.ocaml.ide.structure.OCamlStructureViewElement

abstract class OCamlStructureViewBaseFilter : Filter {
    override fun isVisible(treeNode: TreeElement): Boolean {
        return isVisible((treeNode as OCamlStructureViewElement).value!!)
    }

    protected open fun isVisible(element: PsiElement): Boolean = true

    override fun isReverted(): Boolean = true
}