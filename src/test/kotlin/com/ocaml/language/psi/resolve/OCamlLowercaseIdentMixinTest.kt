package com.ocaml.language.psi.resolve

import com.ocaml.language.psi.OCamlLowercaseIdent
import org.junit.Test

class OCamlLowercaseIdentMixinTest : OCamlBaseResolveTestCase() {

    @Test
    fun test_resolve_let() {
        configureCode("A.ml", "let x = 5")
        configureCode("B.ml", "let y = x")
        assertReferenceToVariableEquals<OCamlLowercaseIdent>("x", "x")
    }

    @Test
    fun test_resolve_val() {
        configureCode("A.mli", "val x : int")
        configureCode("B.ml", "let y = x")
        assertReferenceToVariableEquals<OCamlLowercaseIdent>("x", "x")
    }

    @Test
    fun test_resolve_type() {
        configureCode("A.ml", "type unit = ()")
        configureCode("B.ml", "let y : unit = ()")
        assertReferenceToVariableEquals<OCamlLowercaseIdent>("unit", "unit")

        configureCode("A.ml", "type unit = ()")
        configureCode("B.ml", "type t = unit")
        assertReferenceToVariableEquals<OCamlLowercaseIdent>("unit", "unit")
    }
}