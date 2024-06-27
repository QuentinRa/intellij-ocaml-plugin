package com.ocaml.language.psi.stubs.index

import com.intellij.psi.StubBuilder
import com.ocaml.language.psi.api.OCamlNamedElement
import com.ocaml.language.psi.files.OCamlInterfaceFileStub
import org.junit.Test

class OCamlValIndexTest : BaseIndexTestCase<OCamlNamedElement>() {
    override val builder: StubBuilder
        get() = OCamlInterfaceFileStub.Type.builder

    @Test
    fun test_simple_statements() {
        // Test duplicate, nested, and anonymous
        val indexSink = testIndex("A.mli", """
                val a : unit
                val a : unit
                module X : sig val c : unit end
            """)
        assertEquals(2, indexSink.total)
        assertEquals(2, indexSink.namedIndexValuesCount["A.a"])
        assertEquals(null, indexSink.namedIndexValuesCount["A.X.c"])
    }
}