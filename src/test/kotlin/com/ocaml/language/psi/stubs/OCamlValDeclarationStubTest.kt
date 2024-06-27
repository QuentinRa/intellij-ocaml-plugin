package com.ocaml.language.psi.stubs

import com.intellij.psi.StubBuilder
import com.ocaml.language.psi.files.OCamlInterfaceFileStub
import com.ocaml.language.psi.stubs.impl.OCamlValBindingStub
import org.junit.Test

class OCamlValDeclarationStubTest : BaseStubTestCase() {

    override val builder: StubBuilder
        get() = OCamlInterfaceFileStub.Type.builder

    @Test
    fun test_basic_tree() {
        val nodes = generateOCamlInterfaceStubTree<OCamlValBindingStub>("""
                val a : unit
                val b : unit
            """, 2)
        assertEquals("a", nodes[0].name)
        assertEquals("b", nodes[1].name)
    }

    @Test
    fun test_nested_global_variable() {
        generateOCamlInterfaceStubTree<OCamlValBindingStub>("""
            module X : sig val u : unit end
            module type X = sig val u : unit end
            class type x = object val u : unit end
        """, 0)
    }
}