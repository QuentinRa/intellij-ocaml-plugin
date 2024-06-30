package com.ocaml.language.psi.mixin

import com.ocaml.language.psi.OCamlLowercaseIdent
import com.ocaml.language.psi.resolve.OCamlBaseResolveTestCase
import org.junit.Test

class OCamlLowercaseIdentMixinTest : OCamlBaseResolveTestCase() {

    @Test
    fun test_resolve() {
        configureCode("A.ml", "type unit = ()")
        configureCode("B.ml", "let y : unit = ()")
        assertReferenceToVariableEquals<OCamlLowercaseIdent>("unit", "unit")
    }

    @Test
    fun test_resolve_xxx() {
        configureCode("A.ml", "type unit = ()")
        configureCode("B.ml", "type t = unit")
        assertReferenceToVariableEquals<OCamlLowercaseIdent>("unit", "unit")
    }
}