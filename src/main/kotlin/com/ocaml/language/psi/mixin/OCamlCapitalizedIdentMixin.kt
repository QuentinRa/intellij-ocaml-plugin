package com.ocaml.language.psi.mixin

import com.intellij.psi.PsiReference
import com.intellij.psi.tree.IElementType
import com.ocaml.language.psi.OCamlLowercaseIdent
import com.ocaml.language.psi.api.BaseOCamlReference
import com.ocaml.language.psi.api.OCamlElementImpl

abstract class OCamlCapitalizedIdentMixin(type: IElementType) : OCamlElementImpl(type), OCamlLowercaseIdent {
    override fun getReference(): PsiReference? {
        return OCamlCapitalizedIdentReference(this)
    }
}

class OCamlCapitalizedIdentReference(element: OCamlCapitalizedIdentMixin) : BaseOCamlReference(element)