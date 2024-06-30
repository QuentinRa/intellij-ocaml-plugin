package com.ocaml.language.psi.api

import com.intellij.psi.*
import com.ocaml.language.psi.files.OCamlInterfaceFile
import com.ocaml.language.psi.stubs.index.OCamlLetFQNIndex
import com.ocaml.language.psi.stubs.index.OCamlTypeFQNIndex
import com.ocaml.language.psi.stubs.index.OCamlValFQNIndex

abstract class BaseOCamlReference(element: PsiElement) : PsiReferenceBase<PsiElement>(element), PsiPolyVariantReference {
    // when we return null, the IDE will check multiResolve
    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else null
    }

    // but, there may be a case, such as when resolving documentation
    // in which multiResolve is NOT called, so I have to manually invoke this method
    fun resolveFirst(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else {
            val definitions = resolveResults.filter { it.element?.containingFile is OCamlInterfaceFile }
            definitions.firstOrNull()?.element ?: resolveResults[0].element
        }
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val propertiesFiles = mutableListOf<PsiElement>()
        return PsiElementResolveResult.createResults(propertiesFiles)
    }
}

abstract class BaseOCamlFQNReference(element: PsiElement) : BaseOCamlReference(element) {
    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val propertiesFiles = mutableListOf<PsiElement>()
        propertiesFiles += OCamlLetFQNIndex.Utils.findElementsByName(element.project, element.text)
        propertiesFiles += OCamlValFQNIndex.Utils.findElementsByName(element.project, element.text)
        propertiesFiles += OCamlTypeFQNIndex.Utils.findElementsByName(element.project, element.text)
        return PsiElementResolveResult.createResults(propertiesFiles)
    }
}