package com.ocaml.language.psi.stubs.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.ocaml.language.psi.OCamlTypedef
import com.ocaml.language.psi.impl.OCamlTypedefImpl
import com.ocaml.language.psi.stubs.OCamlBaseNamedStub
import com.ocaml.language.psi.stubs.index.OCamlLowercaseIdentIndex
import com.ocaml.language.psi.stubs.index.OCamlTypesFQNIndex

class OCamlTypeDefStub(parent: StubElement<*>?, elementType: IStubElementType<*, *>, name: String?, qualifiedName: String?) :
    OCamlBaseNamedStub<OCamlTypedef>(parent, elementType, name, qualifiedName) {

    object Type : OCamlBaseNamedStub.Type<OCamlTypeDefStub, OCamlTypedef>("TYPEDEF") {
        override fun createPsi(stub: OCamlTypeDefStub) = OCamlTypedefImpl(stub, this)

        override fun createStub(psi: OCamlTypedef, parentStub: StubElement<out PsiElement>?) =
            OCamlTypeDefStub(parentStub, this, psi.name, psi.qualifiedName)

        override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?) = OCamlTypeDefStub(
            parentStub, this, dataStream.readName()?.string, dataStream.readName()?.string
        )

        override fun indexStub(stub: OCamlTypeDefStub, sink: IndexSink) {
            stub.name?.let { OCamlLowercaseIdentIndex.Utils.index(sink, it) }
            stub.qualifiedName?.let { OCamlTypesFQNIndex.Utils.index(sink, it) }
        }
    }
}