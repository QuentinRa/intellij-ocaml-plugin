package com.ocaml.ide.annotator

import com.ocaml.ide.OCamlBasePlatformTestCase
import com.ocaml.ide.colors.OCamlColor
import org.junit.Test

class OCamlHighlightingAnnotatorTest : OCamlBasePlatformTestCase() {

    @Test
    fun test_global_variable_highlight() {
        configureHighlight("dummy.ml", "let <info>x</info> = 5", OCamlColor.GLOBAL_VARIABLE)
    }

    @Test
    fun test_local_variable_highlight() {
        configureHighlight("dummy.ml", "let x = let <info>y</info> = 5 in y * y", OCamlColor.LOCAL_VARIABLE, true)
    }

    @Test
    fun test_function() {
        configureHighlight("dummy.ml", "let <info>f</info> a b = ()", OCamlColor.FUNCTION_DECLARATION)
    }

    @Test
    fun test_type() {
        configureHighlight("dummy.ml", "type <info>f</info> = ()", OCamlColor.TYPE)
    }

    @Test
    fun test_global_variable_declaration_highlight() {
        configureHighlight("dummy.mli", "val <info>x</info>: int", OCamlColor.GLOBAL_VARIABLE)
    }

    @Test
    fun test_function_declaration() {
        configureHighlight("dummy.mli", "val <info>f</info>: unit -> unit", OCamlColor.FUNCTION_DECLARATION)
    }
}