package com.ocaml.language.psi.stubs.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.*
import com.ocaml.language.psi.OCamlValueBinding
import com.ocaml.language.psi.impl.OCamlValueBindingImpl
import com.ocaml.language.psi.stubs.OCamlBaseNamedStub

class OCamlValBindingStub(parent: StubElement<*>?, elementType: IStubElementType<*, *>, name: String?, qualifiedName: String?) :
    OCamlBaseNamedStub<OCamlValueBinding>(parent, elementType, name, qualifiedName) {

    object Type : OCamlBaseNamedStub.Type<OCamlValBindingStub, OCamlValueBinding>("VALUE_BINDING") {
        override fun createPsi(stub: OCamlValBindingStub) = OCamlValueBindingImpl(stub, this)

        override fun createStub(psi: OCamlValueBinding, parentStub: StubElement<out PsiElement>?) =
            OCamlValBindingStub(parentStub, this, psi.name, psi.qualifiedName)

        override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?) = OCamlValBindingStub(
            parentStub, this, dataStream.readName()?.string, dataStream.readName()?.string
        )
    }
}