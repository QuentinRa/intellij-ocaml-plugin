package com.ocaml.ide.lineMarkers

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.ocaml.icons.OCamlIcons
import com.ocaml.language.psi.OCamlImplUtils.toLeaf
import com.ocaml.language.psi.OCamlLetBinding
import com.ocaml.language.psi.OCamlValueBinding
import com.ocaml.language.psi.api.OCamlVariableDeclaration
import com.ocaml.language.psi.api.OCamlNameIdentifierOwner
import com.ocaml.language.psi.api.OCamlNamedElement
import com.ocaml.language.psi.mixin.utils.computeValueNames
import com.ocaml.language.psi.mixin.utils.expandLetBindingStructuredName
import com.ocaml.language.psi.stubs.index.*

// For tests:
// - In ML, can go to ML or MLI for VAL
// - In MLI, can go to ML for LET
class OCamlLineMarkerProvider : RelatedItemLineMarkerProvider() {
    internal fun collectNavigationMarkersForTest(element: PsiElement): Collection<RelatedItemLineMarkerInfo<*>> {
        val markers: MutableCollection<RelatedItemLineMarkerInfo<*>> = ArrayList()
        fun recursiveCollectNavigationMarkersForTest(e: PsiElement) {
            collectNavigationMarkers(e, markers)
            e.children.forEach { recursiveCollectNavigationMarkersForTest(it) }
        }
        recursiveCollectNavigationMarkersForTest(element)
        return markers
    }

    override fun collectNavigationMarkers(
        element: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        when (element) {
            is OCamlLetBinding -> {
                // From LET, Resolve VAL
                collectValNavigationMarkers(element, element.project, GlobalSearchScope.allScope(element.project), result)
            }
            is OCamlValueBinding -> {
                // From VAL, Resolve LET
                collectLetNavigationMarkers(element,  element.project, GlobalSearchScope.allScope(element.project), result)
            }
        }
    }

    private fun collectLetNavigationMarkers(element: OCamlNameIdentifierOwner, project: Project, scope: GlobalSearchScope, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        if (element !is OCamlVariableDeclaration || !element.isGlobal()) return
        val qualifiedName = element.qualifiedName ?: return
        processCollectedElements(
            element.nameIdentifier?.toLeaf(),
            OCamlLetFQNIndex.Utils.findElementsByName(project, qualifiedName, scope),
            "let/val", true, result
        )
    }

    private fun collectValNavigationMarkers(
        element: OCamlLetBinding,
        project: Project,
        scope: GlobalSearchScope,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        if (!element.isGlobal()) return
        // Handle variable declarations of multiple variables
        val nameIdentifier = element.nameIdentifier?.toLeaf()
        if (nameIdentifier != null) {
            processCollectedElements(
                nameIdentifier,
                OCamlLetFQNIndex.Utils.findElementsByName(project, element.qualifiedName!!, scope),
                "let/val", false, result
            )
        } else if (element.qualifiedName !== null) {
            val qualifiedNames = expandLetBindingStructuredName(element.qualifiedName!!, true)
            element.computeValueNames().forEachIndexed { index, it ->
                processCollectedElements(
                    it.toLeaf(),
                    OCamlLetFQNIndex.Utils.findElementsByName(project, qualifiedNames[index], scope),
                    "let/val", false, result
                )
            }
        }
    }

    private fun processCollectedElements(element: PsiElement?, elements: Collection<OCamlNamedElement>, text: String, isInterface: Boolean, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        if (elements.isEmpty()) return
        val marker: RelatedItemLineMarkerInfo<PsiElement>? = createMarkerInfo(
            element, isInterface, text, elements
        )
        if (marker != null) result.add(marker)
    }

    private fun <T : OCamlNamedElement?> createMarkerInfo(
        nameIdentifier: PsiElement?, isInterface: Boolean, method: String, relatedElements: Collection<T>?
    ): RelatedItemLineMarkerInfo<PsiElement>? {
        return if (nameIdentifier != null && !relatedElements.isNullOrEmpty()) {
            NavigationGutterIconBuilder.create(if (isInterface) OCamlIcons.Gutter.IMPLEMENTED else OCamlIcons.Gutter.IMPLEMENTING)
                .setTooltipText((if (isInterface) "Implements " else "Declare ") + method)
                .setAlignment(GutterIconRenderer.Alignment.RIGHT).setTargets(relatedElements)
                .createLineMarkerInfo(nameIdentifier)
        } else null
    }
}
