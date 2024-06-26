package com.ocaml.language.psi.stubs.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.*
import com.ocaml.language.psi.OCamlValueDescription
import com.ocaml.language.psi.impl.OCamlValueDescriptionImpl
import com.ocaml.language.psi.stubs.OCamlBaseNamedStub

class OCamlValBindingStub(parent: StubElement<*>?, elementType: IStubElementType<*, *>, name: String?, qualifiedName: String?) :
    OCamlBaseNamedStub<OCamlValueDescription>(parent, elementType, name, qualifiedName) {

    object Type : OCamlBaseNamedStub.Type<OCamlValBindingStub, OCamlValueDescription>("VALUE_DESCRIPTION") {
        override fun createPsi(stub: OCamlValBindingStub) = OCamlValueDescriptionImpl(stub, this)

        override fun createStub(psi: OCamlValueDescription, parentStub: StubElement<out PsiElement>?) =
            OCamlValBindingStub(parentStub, this, psi.name, psi.qualifiedName)

        override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?) = OCamlValBindingStub(
            parentStub, this, dataStream.readName()?.string, dataStream.readName()?.string
        )
    }
}