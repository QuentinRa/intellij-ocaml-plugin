package com.ocaml.language.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.stubs.IStubElementType
import com.ocaml.language.psi.OCamlTypedef
import com.ocaml.language.psi.api.OCamlStubbedNamedElementImpl
import com.ocaml.language.psi.api.isAnonymous
import com.ocaml.language.psi.stubs.impl.OCamlTypeDefStub

abstract class OCamlTypeDefMixin : OCamlStubbedNamedElementImpl<OCamlTypeDefStub>, OCamlTypedef {
    constructor(node: ASTNode) : super(node)
    constructor(stub: OCamlTypeDefStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameIdentifier(): PsiElement? {
        val name = typeconstrName.lowercaseIdent
        return if ((name as? LeafPsiElement)?.isAnonymous() == true)
            null
        else
            name
    }
}