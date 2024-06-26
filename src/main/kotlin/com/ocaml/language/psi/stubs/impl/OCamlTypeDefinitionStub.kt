package com.ocaml.language.psi.stubs.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.ocaml.language.psi.OCamlTypeDefinition
import com.ocaml.language.psi.impl.OCamlTypeDefinitionImpl
import com.ocaml.language.psi.stubs.OCamlBaseNamedStub

class OCamlTypeDefinitionStub(parent: StubElement<*>?, elementType: IStubElementType<*, *>, name: String?, qualifiedName: String?) :
    OCamlBaseNamedStub<OCamlTypeDefinition>(parent, elementType, name, qualifiedName) {

    object Type : OCamlBaseNamedStub.Type<OCamlTypeDefinitionStub, OCamlTypeDefinition>("TYPE_DEFINITION") {
        override fun createPsi(stub: OCamlTypeDefinitionStub) = OCamlTypeDefinitionImpl(stub, this)

        override fun createStub(psi: OCamlTypeDefinition, parentStub: StubElement<out PsiElement>?) =
            OCamlTypeDefinitionStub(parentStub, this, psi.name, psi.qualifiedName)

        override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?) = OCamlTypeDefinitionStub(
            parentStub, this, dataStream.readName()?.string, dataStream.readName()?.string
        )
    }
}