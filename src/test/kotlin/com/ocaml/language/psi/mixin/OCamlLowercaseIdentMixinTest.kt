package com.ocaml.language.psi.mixin

import com.ocaml.ide.OCamlBasePlatformTestCase
import com.ocaml.language.psi.OCamlLowercaseIdent
import org.junit.Test

class OCamlLowercaseIdentMixinTest : OCamlBasePlatformTestCase() {

    @Test
    fun test_resolve() {
        configureCode("A.ml", "let y : unit = ()")
        val type = myFixture.findElementByText("unit", OCamlLowercaseIdent::class.java)
        val reference = type.reference?.resolve()
        println(reference)
    }

    @Test
    fun test_resolve_xxx() {
        configureCode("A.ml", "type t = unit")

        val xxx = myFixture.findElementByText("unit", OCamlLowercaseIdent::class.java)
        println(xxx)
    }
}