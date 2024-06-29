package com.ocaml.language.psi.mixin

import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.ocaml.language.OCamlParsingTestCase
import com.ocaml.language.psi.OCamlImplUtils.Companion.toLeaf
import com.ocaml.language.psi.OCamlTypedef
import org.junit.Test

class OCamlTypeBindingMixinTest : OCamlParsingTestCase() {
    private var simpleType: OCamlTypedef? = null
    private var abstractType: OCamlTypedef? = null

    override fun setUp() {
        super.setUp()
        val typeBindings = initWith<OCamlTypedef>("""
            type a = unit
            type a
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
        assertInstanceOf(simpleType?.nameIdentifier?.toLeaf(), LeafPsiElement::class.java)
        assertInstanceOf(abstractType?.nameIdentifier?.toLeaf(), LeafPsiElement::class.java)
    }
}