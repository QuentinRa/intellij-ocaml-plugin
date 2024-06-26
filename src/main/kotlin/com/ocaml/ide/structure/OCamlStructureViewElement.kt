package com.ocaml.ide.structure

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.TreeAnchorizer
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.ui.Queryable
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiElement
import com.intellij.psi.util.childrenOfType
import com.intellij.ui.RowIcon
import com.intellij.util.PlatformIcons
import com.intellij.util.containers.map2Array
import com.ocaml.ide.presentation.getPresentationForStructure
import com.ocaml.language.psi.OCamlLetBindings
import com.ocaml.language.psi.OCamlValueDescription
import com.ocaml.language.psi.api.isAnonymous
import com.ocaml.language.psi.files.OCamlFile
import com.ocaml.language.psi.files.OCamlInterfaceFile
import com.ocaml.language.psi.mixin.handleStructuredLetBinding

class OCamlStructureViewElement(element: PsiElement) : StructureViewTreeElement, Queryable {
    private val psiAnchor = TreeAnchorizer.getService().createAnchor(element)
    private val myElement: PsiElement? get() = TreeAnchorizer.getService().retrieveElement(psiAnchor) as? PsiElement
    private val childElements: List<PsiElement>
        get() {
            return when (val psi = myElement) {
                is OCamlFile -> {
                    psi.childrenOfType<OCamlLetBindings>().flatMap {
                        val allBindings = it.letBindingList
                        if (allBindings.size == 1)
                            handleStructuredLetBinding(allBindings[0])
                        else listOf(it)
                    }
                }

                is OCamlInterfaceFile -> {
                    psi.childrenOfType<OCamlValueDescription>()
                        .mapNotNull { it.valueBinding }
                        .filter { !it.isAnonymous() }
                }

                is OCamlLetBindings -> psi.letBindingList.filter { !it.isAnonymous() }
                else -> emptyList()
            }
        }

    override fun getValue(): PsiElement? = myElement
    override fun navigate(requestFocus: Boolean) {
        (myElement as? Navigatable)?.navigate(requestFocus)
    }

    override fun canNavigate(): Boolean = (myElement as? Navigatable)?.canNavigate() == true
    override fun canNavigateToSource(): Boolean = (myElement as? Navigatable)?.canNavigateToSource() == true
    override fun getPresentation(): ItemPresentation {
        return myElement?.let(::getPresentationForStructure) ?: PresentationData("unknown", null, null, null)
    }

    override fun getChildren(): Array<out TreeElement> = childElements.map2Array { OCamlStructureViewElement(it) }

    override fun putInfo(info: MutableMap<in String, in String>) {
        val presentation = presentation
        info["name"] = presentation.presentableText ?: ""
        val icon = (presentation.getIcon(false) as? RowIcon)?.allIcons?.getOrNull(1)
        info["visibility"] = when (icon) {
            PlatformIcons.PUBLIC_ICON -> "public"
            PlatformIcons.PRIVATE_ICON -> "private"
            PlatformIcons.PROTECTED_ICON -> "restricted"
            null -> "none"
            else -> "unknown"
        }
    }
}