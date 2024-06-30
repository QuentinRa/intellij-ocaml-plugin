package com.ocaml.language.psi.stubs.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.*
import com.ocaml.language.psi.OCamlLetBinding
import com.ocaml.language.psi.api.*
import com.ocaml.language.psi.createStubIfNotAnonymous
import com.ocaml.language.psi.impl.OCamlLetBindingImpl
import com.ocaml.language.psi.mixin.utils.expandLetBindingStructuredName
import com.ocaml.language.psi.stubs.index.OCamlLetFQNIndex
import com.ocaml.language.psi.stubs.index.OCamlLetIndex

class OCamlLetBindingStub(
    parent: StubElement<*>?,
    elementType: IStubElementType<*, *>,
    override val name: String?,
    override val qualifiedName: String?
) : StubBase<OCamlLetBinding>(parent, elementType), OCamlNamedStub {

    object Type : OCamlStubElementType<OCamlLetBindingStub, OCamlLetBinding>("LET_BINDING") {
        override fun shouldCreateStub(node: ASTNode): Boolean = createStubIfNotAnonymous(node)

        override fun createPsi(stub: OCamlLetBindingStub) = OCamlLetBindingImpl(stub, this)

        override fun createStub(psi: OCamlLetBinding, parentStub: StubElement<*>?) =
            OCamlLetBindingStub(parentStub, this, psi.name, psi.qualifiedName)

        override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?) = OCamlLetBindingStub(
            parentStub, this, dataStream.readName()?.string, dataStream.readName()?.string
        )

        override fun serialize(stub: OCamlLetBindingStub, dataStream: StubOutputStream) = with(dataStream) {
            writeName(stub.name)
            writeName(stub.qualifiedName)
        }

        override fun indexStub(stub: OCamlLetBindingStub, sink: IndexSink) {
            expandLetBindingStructuredName(stub.qualifiedName, true)
                .forEach { OCamlLetFQNIndex.Utils.index(sink, it) }
            expandLetBindingStructuredName(stub.name, false)
                .forEach { OCamlLetIndex.Utils.index(sink, it) }
        }
    }
}