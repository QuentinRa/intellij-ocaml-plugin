package com.ocaml.language.psi.stubs.index

import com.intellij.psi.StubBuilder
import com.ocaml.language.psi.api.OCamlNamedElement
import com.ocaml.language.psi.files.OCamlFileStub
import org.junit.Test

class OCamlLetIndexTest : BaseIndexTestCase<OCamlNamedElement>() {
    override val builder: StubBuilder
        get() = OCamlFileStub.Type.builder

    private val simpleCode = """
                let a = ()
                let a = ()
                let c = 
                    let d = 5
                    in d * d
                module X = struct let x = () end
                let _ = ()
            """

    // every pattern statement is a stub
    // e.g., they are tested in the stubs test category
    // We only need to test one kind of pattern expression here
    private val patternCode = """
                let a,b = ()
            """

    @Test
    fun test_simple_statements_fqn() {
        // Test duplicate, nested, and anonymous
        val indexSink = testFQNIndex("A.ml", simpleCode)
        assertEquals(3, indexSink.total)
        assertEquals(2, indexSink.count("A.a"))
        assertEquals(1, indexSink.count("A.c"))
    }

    @Test
    fun test_pattern_statements_fqn() {
        val indexSink = testFQNIndex("A.ml", patternCode)
        assertEquals(2, indexSink.total)
        assertEquals(1, indexSink.count("A.a"))
        assertEquals(1, indexSink.count("A.b"))
    }

    @Test
    fun test_simple_statements() {
        // Test duplicate, nested, and anonymous
        val indexSink = testIndex("A.ml", simpleCode)
        assertEquals(3, indexSink.total)
        assertEquals(2, indexSink.count("a"))
        assertEquals(1, indexSink.count("c"))
    }

    @Test
    fun test_pattern_statements() {
        val indexSink = testIndex("A.ml", patternCode)
        assertEquals(2, indexSink.total)
        assertEquals(1, indexSink.count("a"))
        assertEquals(1, indexSink.count("b"))
    }
}