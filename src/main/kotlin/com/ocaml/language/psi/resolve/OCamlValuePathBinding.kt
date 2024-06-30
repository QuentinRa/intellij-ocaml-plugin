package com.ocaml.language.psi.resolve

import com.intellij.openapi.util.TextRange
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

class OCamlValuePathManipulator : AbstractElementManipulator<OCamlValuePathMixin>() {
    override fun handleContentChange(element: OCamlValuePathMixin, range: TextRange, newContent: String?): OCamlValuePathMixin? {
        return element
    }
}

class OCamlValuePathReference(element: OCamlValuePathMixin) : BaseOCamlFQNReference(element)