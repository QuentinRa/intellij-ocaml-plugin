package com.ocaml.language.psi.stubs.index

import com.intellij.psi.StubBuilder
import com.ocaml.language.psi.api.OCamlNamedElement
import com.ocaml.language.psi.files.OCamlInterfaceFileStub
import org.junit.Test

class OCamlTypeIndexTest : BaseIndexTestCase<OCamlNamedElement>() {
    override val builder: StubBuilder
        get() = OCamlInterfaceFileStub.Type.builder

    @Test
    fun test_simple_statements() {
        // Test duplicate, nested, and anonymous
        val indexSink = testIndex("A.mli", """
                type u
                type u = unit
                module X : sig type t end
            """)
        assertEquals(2, indexSink.total)
        assertEquals(2, indexSink.count("A.u"))
        assertEquals(null, indexSink.count("A.X.t"))
    }
}