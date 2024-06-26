package com.ocaml.language.psi.stubs

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.*
import com.ocaml.language.psi.OCamlValueDescription
import com.ocaml.language.psi.api.OCamlNamedStub
import com.ocaml.language.psi.api.OCamlStubElementType
import com.ocaml.language.psi.createStubIfNotAnonymous
import com.ocaml.language.psi.impl.OCamlValueDescriptionImpl
import com.ocaml.language.psi.stubs.index.OCamlNamedElementIndex

class OCamlValBindingStub(
    parent: StubElement<*>?,
    elementType: IStubElementType<*, *>,
    override val name: String?,
    override val qualifiedName: String?
) : StubBase<OCamlValueDescription>(parent, elementType), OCamlNamedStub {

    object Type : OCamlStubElementType<OCamlValBindingStub, OCamlValueDescription>("VALUE_DESCRIPTION") {
        override fun shouldCreateStub(node: ASTNode): Boolean = createStubIfNotAnonymous(node)

        override fun createPsi(stub: OCamlValBindingStub) = OCamlValueDescriptionImpl(stub, this)

        override fun createStub(psi: OCamlValueDescription, parentStub: StubElement<*>?) =
            OCamlValBindingStub(parentStub, this, psi.name, psi.qualifiedName)

        override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?) = OCamlValBindingStub(
            parentStub, this, dataStream.readName()?.string, dataStream.readName()?.string
        )

        override fun serialize(stub: OCamlValBindingStub, dataStream: StubOutputStream) = with(dataStream) {
            writeName(stub.name)
            writeName(stub.qualifiedName)
        }

        override fun indexStub(stub: OCamlValBindingStub, sink: IndexSink) {
            stub.qualifiedName?.let { OCamlNamedElementIndex.Utils.index(sink, it) }
        }
    }
}