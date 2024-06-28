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
import com.ocaml.language.base.OCamlFileBase
import com.ocaml.language.psi.OCamlLetBinding
import com.ocaml.language.psi.OCamlLetBindings
import com.ocaml.language.psi.OCamlTypeDefinition
import com.ocaml.language.psi.OCamlValueDescription
import com.ocaml.language.psi.api.OCamlFakeElement
import com.ocaml.language.psi.api.isAnonymous
import com.ocaml.language.psi.files.OCamlFile
import com.ocaml.language.psi.files.OCamlInterfaceFile
import com.ocaml.language.psi.mixin.utils.getNestedLetBindings
import com.ocaml.language.psi.mixin.utils.handleStructuredLetBinding

// A bit complex
//
// The first time this class is called, we are passed a FILE as "element"
// We can then return in "childElements" the direct children.
// Normal scenarios:
// - OCamlValueDescription
//
// But, an element, such as a CLASS in JAVA, can have children too (methods, fields, etc.).
// This is the case with:
// - OCamlLetBindings
// - OCamlTypeDefinition
// In which case, the OCamlStructureViewElement is invoked again with them as "element"
//
// When an element has children, you have to add them in PresentationHandler.kt
// Otherwise, we can't display them (error)


class OCamlStructureViewElement(element: PsiElement, private val useAnchor: Boolean = true) : StructureViewTreeElement, Queryable {
    private val root : OCamlFakeElement? = element as? OCamlFakeElement

    // During tests, the anchor services is not working well with "fake" elements
    private val psiAnchor = if (useAnchor) TreeAnchorizer.getService().createAnchor(element) else element
    private val myElement: PsiElement? get() =
        if (useAnchor) TreeAnchorizer.getService().retrieveElement(psiAnchor) as? PsiElement
        else psiAnchor as? PsiElement

    private val childElements: List<PsiElement>
        get() {
            return when (val psi = myElement) {
                is OCamlFile -> {
                    psi.childrenOfType<OCamlLetBindings>().expand() + collectCommonElements(psi)
                }

                is OCamlInterfaceFile -> {
                    psi.childrenOfType<OCamlValueDescription>()
                        .mapNotNull { it.valueBinding } + collectCommonElements(psi)
                }

                is OCamlLetBindings -> psi.letBindingList.filter { !it.isAnonymous() }
                is OCamlLetBinding -> psi.getNestedLetBindings().expand()
                is OCamlTypeDefinition -> psi.typedefList

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
        return myElement?.let {
            getPresentationForStructure(it, root)
        } ?: PresentationData("unknown", null, null, null)
    }

    override fun getChildren(): Array<out TreeElement> = childElements.map2Array { OCamlStructureViewElement(it, useAnchor) }

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

fun List<OCamlLetBindings>.expand() : List<PsiElement> {
    return this.flatMap {
        val allBindings = it.letBindingList
        if (allBindings.size == 1)
            handleStructuredLetBinding(allBindings[0])
        else listOf(it)
    }
}

fun collectCommonElements(psi: OCamlFileBase) : List<PsiElement> {
    return psi.childrenOfType<OCamlTypeDefinition>().flatMap {
        if (it.typedefList.size == 1) listOf(it.typedefList[0])
        else listOf(it)
    }
}