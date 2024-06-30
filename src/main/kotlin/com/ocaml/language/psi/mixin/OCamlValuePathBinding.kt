package com.ocaml.language.psi.mixin

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.tree.IElementType
import com.ocaml.language.psi.OCamlValuePath
import com.ocaml.language.psi.api.OCamlElementImpl
import com.ocaml.language.psi.files.OCamlInterfaceFile
import com.ocaml.language.psi.stubs.index.*

abstract class OCamlValuePathBindingMixin(type: IElementType) : OCamlElementImpl(type), OCamlValuePath {
    override fun getReference(): PsiReference? {
        return OCamlValuePathReference(this)
    }
}

class OCamlValuePathReference(element: OCamlValuePathBindingMixin) : PsiReferenceBase<PsiElement>(element),
    PsiPolyVariantReference {
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
        propertiesFiles += OCamlLetFQNIndex.Utils.findElementsByName(element.project, element.text)
        propertiesFiles += OCamlValFQNIndex.Utils.findElementsByName(element.project, element.text)
        return PsiElementResolveResult.createResults(propertiesFiles)
    }
}