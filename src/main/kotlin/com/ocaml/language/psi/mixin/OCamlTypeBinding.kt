package com.ocaml.language.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.ocaml.language.psi.OCamlTypeDefinition
import com.ocaml.language.psi.api.OCamlStubbedNamedElementImpl
import com.ocaml.language.psi.stubs.impl.OCamlTypeDefinitionStub

abstract class OCamlTypeBindingMixin : OCamlStubbedNamedElementImpl<OCamlTypeDefinitionStub>, OCamlTypeDefinition {
    constructor(node: ASTNode) : super(node)
    constructor(stub: OCamlTypeDefinitionStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameIdentifier(): PsiElement? {
        val typedef = typedefList.firstOrNull() ?: return null
        return typedef.typeconstrName.lowercaseIdent
    }
}