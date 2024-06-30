package com.ocaml.language.psi.resolve

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.tree.IElementType
import com.ocaml.language.psi.OCamlLowercaseIdent
import com.ocaml.language.psi.api.BaseOCamlReference
import com.ocaml.language.psi.api.OCamlElementImpl
import com.ocaml.language.psi.stubs.index.OCamlLowercaseIdentIndex

abstract class OCamlLowercaseIdentMixin(type: IElementType) : OCamlElementImpl(type), OCamlLowercaseIdent {
    override fun getReference(): PsiReference? {
        return OCamlLowercaseIdentReference(this)
    }
}

class OCamlLowercaseIdentManipulator : AbstractElementManipulator<OCamlLowercaseIdentMixin>() {
    override fun handleContentChange(element: OCamlLowercaseIdentMixin, range: TextRange, newContent: String?): OCamlLowercaseIdentMixin? {
        return element
    }
}

class OCamlLowercaseIdentReference(element: OCamlLowercaseIdentMixin) : BaseOCamlReference(element) {
    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val propertiesFiles = mutableListOf<PsiElement>()
        propertiesFiles += OCamlLowercaseIdentIndex.Utils.findElementsByName(element.project, element.text)
        return PsiElementResolveResult.createResults(propertiesFiles)
    }
}