package com.ocaml.language.psi.stubs

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.*
import com.ocaml.language.psi.api.*
import com.ocaml.language.psi.createStubIfNotAnonymous

/**
 * A class created in a effort of reducing the number of copy-pastes
 * due to the logic being the same but the types being different.
 */
open class OCamlBaseNamedStub<T>(
    parent: StubElement<*>?,
    elementType: IStubElementType<*, *>,
    override val name: String?,
    override val qualifiedName: String?
) : StubBase<T>(parent, elementType), OCamlNamedStub where T : OCamlElement {

    abstract class Type<R, T>(debugName: String) : OCamlStubElementType<R, T>(debugName)
        where R : OCamlBaseNamedStub<T>, T : OCamlNameIdentifierOwner
    {
        override fun shouldCreateStub(node: ASTNode): Boolean = createStubIfNotAnonymous(node)

        override fun serialize(stub: R, dataStream: StubOutputStream) = with(dataStream) {
            writeName(stub.name)
            writeName(stub.qualifiedName)
        }
    }
}