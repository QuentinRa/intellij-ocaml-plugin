package com.ocaml.language.psi.mixin

import com.ocaml.language.OCamlInterfaceParsingTestCase
import com.ocaml.language.psi.OCamlValueBinding
import org.junit.Test

class OCamlValBindingMixinTest : OCamlInterfaceParsingTestCase() {
    private var valSimple: OCamlValueBinding? = null
    private var valDeconstruction: OCamlValueBinding? = null
    private var valDeconstructionComplex: OCamlValueBinding? = null
    private var valOperatorName: OCamlValueBinding? = null
    private var valOperatorNameNoSpace: OCamlValueBinding? = null

    override fun setUp() {
        super.setUp()
        val valBindings = initWith<OCamlValueBinding>("""
            val a : unit
            val b : unit * unit
            val cd : (unit * unit) * unit
            val ( + ) : unit
            val (+) : unit
        """)

        valSimple = valBindings[0]
        valDeconstruction = valBindings[1]
        valDeconstructionComplex = valBindings[2]
        valOperatorName = valBindings[3]
        valOperatorNameNoSpace = valBindings[4]
    }

    override fun tearDown() {
        super.tearDown()
        valSimple = null
        valDeconstruction = null
        valDeconstructionComplex = null
        valOperatorName = null
        valOperatorNameNoSpace = null
    }

    @Test
    fun test_name_identifier_is_leaf() {
        assertIsNameIdentifierALeaf(valSimple?.nameIdentifier)
        assertIsNameIdentifierALeaf(valDeconstruction?.nameIdentifier)
        assertIsNameIdentifierALeaf(valDeconstructionComplex?.nameIdentifier)
        assertIsNameIdentifierALeaf(valOperatorName?.nameIdentifier)
        assertIsNameIdentifierALeaf(valOperatorNameNoSpace?.nameIdentifier)
    }

    @Test
    fun test_name() {
        assertEquals("a", valSimple?.name)
        assertEquals("b", valDeconstruction?.name)
        assertEquals("cd", valDeconstructionComplex?.name)
        assertEquals("( + )", valOperatorName?.name)
        assertEquals("( + )", valOperatorNameNoSpace?.name)
    }
}