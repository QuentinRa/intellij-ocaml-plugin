package com.ocaml.language.psi.mixin

import com.intellij.psi.*
import com.intellij.psi.tree.IElementType
import com.ocaml.language.psi.OCamlValuePath
import com.ocaml.language.psi.api.BaseOCamlFQNReference
import com.ocaml.language.psi.api.OCamlElementImpl

abstract class OCamlValuePathMixin(type: IElementType) : OCamlElementImpl(type), OCamlValuePath {
    override fun getReference(): PsiReference? {
        return OCamlValuePathReference(this)
    }
}

class OCamlValuePathReference(element: OCamlValuePathMixin) : BaseOCamlFQNReference(element)