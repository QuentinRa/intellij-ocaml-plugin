package com.ocaml.ide.annotator

import com.ocaml.ide.OCamlBasePlatformTestCase
import com.ocaml.ide.colors.OCamlColor
import org.junit.Test

class OCamlHighlightingAnnotatorTest : OCamlBasePlatformTestCase() {

    @Test
    fun testGlobalVariableHighlight() {
        configureHighlight("dummy.ml", "let <info>x</info> = 5", OCamlColor.GLOBAL_VARIABLE)
    }

    @Test
    fun testLocalVariableHighlight() {
        configureHighlight("dummy.ml", "let x = let <info>y</info> = 5 in y * y", OCamlColor.LOCAL_VARIABLE, true)
    }

    @Test
    fun testFunction() {
        configureHighlight("dummy.ml", "let <info>f</info> a b = ()", OCamlColor.FUNCTION_DECLARATION)
    }

    @Test
    fun testType() {
        configureHighlight("dummy.ml", "type <info>f</info> = ()", OCamlColor.TYPE)
    }

    @Test
    fun testGlobalVariableDeclarationHighlight() {
        configureHighlight("dummy.mli", "val <info>x</info>: int", OCamlColor.GLOBAL_VARIABLE)
    }

    @Test
    fun testFunctionDeclaration() {
        configureHighlight("dummy.mli", "val <info>f</info>: unit -> unit", OCamlColor.FUNCTION_DECLARATION)
    }
}