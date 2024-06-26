package com.ocaml.language.psi.stubs

import com.intellij.psi.stubs.StubElement
import com.ocaml.ide.OCamlBasePlatformTestCase
import com.ocaml.language.psi.files.OCamlFileStub
import com.ocaml.language.psi.stubs.impl.OCamlLetBindingStub
import org.junit.Test

class OCamlLetBindingStubTest : OCamlBasePlatformTestCase() {
    protected fun <T: StubElement<*>> generateStubTree(code: String, expectedChildren: Int): List<T> {
        val file = configureCode("A.ml", code)
        val stubTree = OCamlFileStub.Type.builder.buildStubTree(file)
        assertSize(expectedChildren, stubTree.childrenStubs)
        @Suppress("UNCHECKED_CAST")
        return stubTree.childrenStubs as List<T>
    }

    @Test
    fun testBasicTree() {
        val nodes = generateStubTree<OCamlLetBindingStub>("""
                let a = ()
                let b = ()
            """, 2)
        assertEquals("a", nodes[0].name)
        assertEquals("b", nodes[1].name)
    }

    @Test
    fun testAnonymous() {
        generateStubTree<OCamlLetBindingStub>("""
                let _ = ()
                let _ = 
                    let d = 5
                    in d * d
            """, 0)
    }

    @Test
    fun testLocalVariable() {
        generateStubTree<OCamlLetBindingStub>("""
                let c = 
                    let d = 5
                    in d * d
            """, 1)
    }

    @Test
    fun testNestedGlobalVariable() {
        generateStubTree<OCamlLetBindingStub>("""
                class xxx = let x = () in object end;;
                module xxx = struct let x = () end;;
            """, 0)
    }
}