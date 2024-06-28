package com.ocaml.ide.structure

import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.impl.java.VisibilitySorter
import com.intellij.ide.util.treeView.smartTree.Filter
import com.intellij.ide.util.treeView.smartTree.Grouper
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.ocaml.ide.structure.filters.*
import com.ocaml.language.base.OCamlFileBase
import com.ocaml.language.psi.OCamlLetBinding
import com.ocaml.language.psi.OCamlLetBindings

// JavaFileTreeModel
class OCamlStructureViewModel(editor: Editor?, psiFile: PsiFile, useAnchor: Boolean = true) :
    StructureViewModelBase(psiFile, editor, OCamlStructureViewElement(psiFile, useAnchor)),
    StructureViewModel.ElementInfoProvider {

    override fun getSorters(): Array<Sorter> = arrayOf(Sorter.ALPHA_SORTER, VisibilitySorter.INSTANCE)
    override fun getFilters(): Array<Filter> = arrayOf(
        OCamlStructureViewVariablesFilter,
        OCamlStructureViewFunctionsFilter
    )
    override fun getGroupers(): Array<Grouper> = super.getGroupers()

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean = element.value is OCamlFileBase
    override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean = when (element.value) {
        is OCamlFileBase -> false
        is OCamlLetBinding -> false
        is OCamlLetBindings -> false
        else -> true
    }
}
