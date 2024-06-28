package com.ocaml.language.psi.stubs.index

import com.intellij.psi.StubBuilder
import com.ocaml.language.psi.api.OCamlNamedElement
import com.ocaml.language.psi.files.OCamlFileStub
import org.junit.Test

class OCamlLetIndexTest : BaseIndexTestCase<OCamlNamedElement>() {
    override val builder: StubBuilder
        get() = OCamlFileStub.Type.builder

    @Test
    fun test_simple_statements() {
        // Test duplicate, nested, and anonymous
        val indexSink = testFQNIndex("A.ml", """
                let a = ()
                let a = ()
                let c = 
                    let d = 5
                    in d * d
                module X = struct let x = () end
                let _ = ()
            """)
        assertEquals(3, indexSink.total)
        assertEquals(2, indexSink.count("A.a"))
        assertEquals(1, indexSink.count("A.c"))
        assertEquals(null, indexSink.count("A.X.x"))
    }

    @Test
    fun test_pattern_statements() {
        // every pattern statement is a stub
        // e.g., they are tested in the stubs category
        // We only need to test one pattern here
        val indexSink = testFQNIndex("A.ml", """
                let a,b = ()
            """)
        assertEquals(2, indexSink.total)
        assertEquals(1, indexSink.count("A.a"))
        assertEquals(1, indexSink.count("A.b"))
    }
}