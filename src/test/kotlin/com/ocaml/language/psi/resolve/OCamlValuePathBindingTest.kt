package com.ocaml.language.psi.resolve

import org.junit.Test

class OCamlValuePathBindingTest : OCamlBaseResolveTestCase() {

    @Test
    fun test_resolve_let() {
        configureCode("A.ml", "let x = 5")
        configureCode("B.ml", "let y = A.x")
        assertReferenceToVariableEquals<OCamlValuePathMixin>("A.x", "x")
    }

    @Test
    fun test_resolve_val() {
        configureCode("A.mli", "val x : int")
        configureCode("B.ml", "let y = A.x")
        assertReferenceToVariableEquals<OCamlValuePathMixin>("A.x", "x")
    }

    @Test
    fun test_resolve_type() {
        // syntactically OK but not valid
        configureCode("A.mli", "type none = unit")
        configureCode("B.ml", "let test = A.none")
        assertReferenceToVariableEquals<OCamlValuePathMixin>("A.none", "none")
    }
}