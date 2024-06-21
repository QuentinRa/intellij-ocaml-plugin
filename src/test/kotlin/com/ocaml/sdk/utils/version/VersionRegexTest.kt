package com.ocaml.sdk.utils.version

import com.intellij.testFramework.UsefulTestCase
import com.ocaml.sdk.utils.OCamlSdkVersionUtils.isValid
import org.junit.Test

@Suppress("JUnitMixedFramework")
class VersionRegexTest : UsefulTestCase() {
    // invalid
    @Test
    fun test1d() {
        assertFalse(isValid("4"))
    }

    @Test
    fun test1dEmpty() {
        assertFalse(isValid("4."))
    }

    @Test
    fun test1d1d() {
        assertFalse(isValid("4.0"))
    }

    @Test
    fun test1d1dEmpty() {
        assertFalse(isValid("4.0."))
    }

    @Test
    fun test1d1d1d() {
        assertFalse(isValid("4.0.0"))
    }

    @Test
    fun testInvalidStart() {
        // now, this is valid :D
        assertTrue(isValid("ocaml-4.05.0"))
    }

    // valid
    @Test
    fun test1d2d() {
        assertTrue(isValid("4.05"))
    }

    @Test
    fun test1d2dKind() {
        assertTrue(isValid("4.05+mingw64c"))
    }

    @Test
    fun test1d2d1d() {
        assertTrue(isValid("4.05.0"))
    }

    @Test
    fun test1d2d1dKind() {
        assertTrue(isValid("4.05.0+mingw64c"))
    }

    @Test
    fun test1d2d1d2Kinds() {
        assertTrue(isValid("4.05.0+trunk+afl"))
    }

    @Test
    fun test1d2d1d3Kinds() {
        assertTrue(isValid("4.05.0+musl+static+flambda"))
    }

    @Test
    fun test1d2d1dKindsAndMultiWord() {
        assertTrue(isValid("4.05.0+flambda+no-float-float-array"))
    }

    @Test
    fun test1d2d1dAlpha() {
        assertTrue(isValid("4.05.0~alpha1"))
    }

    @Test
    fun test1d2d1dAlphaKind() {
        assertTrue(isValid("4.05.0+0~alpha1+options"))
    }

    @Test
    fun test1d2d1dSpecial() {
        assertFalse(isValid("4.05.0+flambda+no float float array"))
        assertFalse(isValid("4.05.0+flambda+*"))
        assertFalse(isValid("4.05.0+flambda+/"))
        assertFalse(isValid("4.05.0+flambda+%"))
        assertFalse(isValid("4.05.0+flambda+_"))
        assertFalse(isValid("4.05.0+flambda+@"))
        assertFalse(isValid("4.05.0+flambda+NOT"))
        assertFalse(isValid("4.05.0+flambda++"))
    }

    @Test
    fun test1d2d1dOCamlBaseCompiler() {
        assertTrue(isValid("ocaml-base-compiler.4.05.0"))
        assertTrue(isValid("ocaml-base-compiler.4.05.0+flambda"))
        assertTrue(isValid("ocaml-base-compiler.4.05.0+flambda+options"))
        assertTrue(isValid("ocaml-base-compiler.4.05.0~alpha1"))
        assertTrue(isValid("ocaml-base-compiler.4.05.0~alpha1+options"))
    }

    @Test
    fun testTricky() {
        // :D
        assertFalse(isValid("4.05.0.4.05.0"))
    }

    @Test
    fun test1d2d1dFake() {
        assertFalse(isValid("4.05.0-v"))
        assertFalse(isValid("4.05.0-v2"))
        assertFalse(isValid("4.05.0-"))
    }
}
