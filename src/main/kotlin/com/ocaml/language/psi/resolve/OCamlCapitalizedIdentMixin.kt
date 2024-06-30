package com.ocaml.language.psi.resolve

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.tree.IElementType
import com.ocaml.language.psi.OCamlLowercaseIdent
import com.ocaml.language.psi.api.BaseOCamlReference
import com.ocaml.language.psi.api.OCamlElementImpl

abstract class OCamlCapitalizedIdentMixin(type: IElementType) : OCamlElementImpl(type), OCamlLowercaseIdent {
    override fun getReference(): PsiReference? {
        return OCamlCapitalizedIdentReference(this)
    }
}

class OCamlCapitalizedIdentManipulator : AbstractElementManipulator<OCamlCapitalizedIdentMixin>() {
    override fun handleContentChange(element: OCamlCapitalizedIdentMixin, range: TextRange, newContent: String?): OCamlCapitalizedIdentMixin? {
        return element
    }
}

class OCamlCapitalizedIdentReference(element: OCamlCapitalizedIdentMixin) : BaseOCamlReference(element)