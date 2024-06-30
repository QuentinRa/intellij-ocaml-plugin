package com.ocaml.language.psi.mixin

import com.ocaml.ide.OCamlBasePlatformTestCase
import com.ocaml.language.psi.api.OCamlNameIdentifierOwner
import org.junit.Test

class OCamlValuePathBindingTest : OCamlBasePlatformTestCase() {

    @Test
    fun test_resolve() {
        configureCode("A.ml", "let x = 5")
        configureCode("B.ml", "open A\nlet y = A.x")

        val valuePath = myFixture.findElementByText("A.x", OCamlValuePathBindingMixin::class.java)
        val reference = valuePath.reference?.resolve()
        assertNotNull(valuePath) ; valuePath!!
        assertNotNull(reference) ; reference!!
        assertSize(1, valuePath.references)
        assertEquals(reference, valuePath.references[0].resolve())

        reference as OCamlNameIdentifierOwner
        assertEquals("x", reference.name)
    }
}