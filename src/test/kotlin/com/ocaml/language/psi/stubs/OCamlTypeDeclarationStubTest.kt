package com.ocaml.language.psi.stubs

import com.intellij.psi.StubBuilder
import com.ocaml.language.psi.files.OCamlInterfaceFileStub
import com.ocaml.language.psi.stubs.impl.OCamlTypeDefStub
import org.junit.Test

class OCamlTypeDeclarationStubTest : BaseStubTestCase() {

    override val builder: StubBuilder
        get() = OCamlInterfaceFileStub.Type.builder

    @Test
    fun test_basic_tree() {
        val nodes = generateOCamlInterfaceStubTree<OCamlTypeDefStub>("""
                type a = unit
                type b = unit
            """, 2)
        assertEquals("a", nodes[0].name)
        assertEquals("b", nodes[1].name)
    }

    @Test
    fun test_nested_global_variable() {
        generateOCamlInterfaceStubTree<OCamlTypeDefStub>("""
            module X : sig type u = unit end
            module type X = sig type u = unit end
        """, 0)
    }
}