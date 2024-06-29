package com.ocaml.language.psi.mixin

import com.ocaml.language.OCamlParsingTestCase
import com.ocaml.language.psi.OCamlTypedef
import org.junit.Test

class OCamlTypeBindingMixinTest : OCamlParsingTestCase() {
    private var simpleType: OCamlTypedef? = null
    private var abstractType: OCamlTypedef? = null

    override fun setUp() {
        super.setUp()
        val typeBindings = initWith<OCamlTypedef>("""
            type a = unit
            type b
        """)
        simpleType = typeBindings[0]
        abstractType = typeBindings[1]
    }

    override fun tearDown() {
        super.tearDown()
        simpleType = null
        abstractType = null
    }

    @Test
    fun test_name_identifier_is_leaf() {
        assertIsNameIdentifierALeaf(simpleType?.nameIdentifier)
        assertIsNameIdentifierALeaf(abstractType?.nameIdentifier)
    }

    @Test
    fun test_name() {
        assertEquals("a", simpleType?.name)
        assertEquals("b", abstractType?.name)
    }

    @Test
    fun test_qualified_name() {
        assertQualifiedNameEquals(simpleType, "a")
        assertQualifiedNameEquals(abstractType, "b")
    }
}