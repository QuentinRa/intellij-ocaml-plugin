package com.ocaml.language.psi.mixin

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.tree.IElementType
import com.ocaml.language.psi.OCamlValuePath
import com.ocaml.language.psi.api.OCamlElementImpl
import com.ocaml.language.psi.files.OCamlInterfaceFile
import com.ocaml.language.psi.stubs.index.OCamlVariablesIndex


abstract class OCamlValuePathBindingMixin(type: IElementType) : OCamlElementImpl(type), OCamlValuePath {
    override fun getReference(): PsiReference? {
        return OCamlValuePathReference(this)
    }
}

class OCamlValuePathManipulator : AbstractElementManipulator<OCamlValuePathBindingMixin>() {
    override fun handleContentChange(element: OCamlValuePathBindingMixin, range: TextRange, newContent: String?): OCamlValuePathBindingMixin? {
        return element
    }
}

class OCamlValuePathReference(element: OCamlValuePathBindingMixin) : PsiReferenceBase<PsiElement>(element),
    PsiPolyVariantReference {
    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else {
            //
            val definitions = resolveResults.filter { it.element?.containingFile is OCamlInterfaceFile }
            definitions.firstOrNull()?.element ?: resolveResults[0].element
        }
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val propertiesFiles = mutableListOf<PsiElement>()
        propertiesFiles += OCamlVariablesIndex.Utils.findElementsByName(element.project, element.text)
        return PsiElementResolveResult.createResults(propertiesFiles)
    }

}