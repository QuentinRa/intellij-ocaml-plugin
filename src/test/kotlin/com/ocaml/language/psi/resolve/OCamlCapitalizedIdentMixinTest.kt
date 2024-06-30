package com.ocaml.language.psi.resolve

import com.ocaml.ide.OCamlBasePlatformTestCase
import com.ocaml.language.psi.OCamlCapitalizedIdent
import org.junit.Test

class OCamlCapitalizedIdentMixinTest : OCamlBasePlatformTestCase() {

    @Test
    fun test_resolve_yyy() {
        configureCode("A.ml", "let none = None")

        val xxx = myFixture.findElementByText("None", OCamlCapitalizedIdent::class.java)
        println(xxx)
    }

    @Test
    fun test_resolve_zzz() {
        configureCode("A.ml", "let none = Option.None")

        val xxx = myFixture.findElementByText("None", OCamlCapitalizedIdent::class.java)
        println(xxx)
    }
}