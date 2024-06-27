package com.ocaml.language.psi.stubs

import com.intellij.psi.StubBuilder
import com.ocaml.language.psi.files.OCamlFileStub
import com.ocaml.language.psi.stubs.impl.OCamlLetBindingStub
import org.junit.Test

class OCamlLetBindingStubTest : BaseStubTestCase() {

    override val builder: StubBuilder
        get() = OCamlFileStub.Type.builder

    @Test
    fun test_basic_tree() {
        val nodes = generateOCamlStubTree<OCamlLetBindingStub>("""
                let a = ()
                let b = ()
            """, 2)
        assertEquals("a", nodes[0].name)
        assertEquals("b", nodes[1].name)
    }

    @Test
    fun test_anonymous() {
        generateOCamlStubTree<OCamlLetBindingStub>("""
                let _ = ()
                let _ = 
                    let d = 5
                    in d * d
            """, 0)
    }

    @Test
    fun test_local_variable() {
        generateOCamlStubTree<OCamlLetBindingStub>("""
                let c = 
                    let d = 5
                    in d * d
            """, 1)
    }

    @Test
    fun test_nested_global_variable() {
        generateOCamlStubTree<OCamlLetBindingStub>("""
                class xxx = let x = () in object end;;
                module xxx = struct let x = () end;;
            """, 0)
    }
}