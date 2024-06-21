package com.ocaml.sdk.utils

import com.ocaml.OCamlBaseUtilityTest
import com.ocaml.sdk.utils.OCamlSdkVersionUtils.isUnknownVersion
import com.ocaml.sdk.utils.OCamlSdkVersionUtils.parse
import com.ocaml.sdk.utils.OCamlSdkVersionUtils.parseWithoutModifier
import org.junit.Test

class VersionOnlyRegexTest : OCamlBaseUtilityTest() {
    private fun assertIsUnknownVersion(path: String) {
        assertTrue(isUnknownVersion(parseWithoutModifier(path)))
    }

    private fun assertIsVersion(path: String, expectedVersion: String) {
        assertEquals(expectedVersion, parseWithoutModifier(path))
        if (expectedVersion.contains("/")) {
            // TR
            assertEquals(expectedVersion, parse("$path/"))
            // Windows
            assertEquals(expectedVersion, parse(path.replace("/", "\\")))
            // Windows + TR
            assertEquals(expectedVersion, parse(path.replace("/", "\\") + "\\"))
        }
    }

    @Test
    fun testEmpty() {
        assertIsUnknownVersion("")
    }

    @Test
    fun testNotAPath() {
        assertIsUnknownVersion("4.05.0")
    }

    @Test
    fun test2d() {
        assertIsUnknownVersion("/40.5")
        assertIsUnknownVersion("/40.50")
    }

    @Test
    fun test1dThen2d() {
        assertIsVersion("~/.opam/4.05", "4.05")
    }

    @Test
    fun test1dThen2dThenEmpty() {
        assertIsUnknownVersion("~/.opam/4.05.")
    }

    @Test
    fun test1dThen2dThen1d() {
        assertIsVersion("~/.opam/4.05.2", "4.05.2")
    }

    @Test
    fun test1dThen2dThen2d() {
        assertIsUnknownVersion("~/.opam/4.05.02")
    }

    @Test
    fun test1dThen2dThen2dThenMingw() {
        assertIsVersion("~/.opam/4.13.1+mingw64c", "4.13.1")
    }

    @Test
    fun test1dThen2dThen2dThenKinds() {
        assertIsVersion("~/.opam/4.13.1+trunk+afl", "4.13.1")
        assertIsVersion("~/.opam/4.13.1+musl+static+flambda", "4.13.1")
        assertIsVersion("~/.opam/4.13.1+flambda+no-float-float-array", "4.13.1")
        assertIsVersion("~/.opam/4.05.0~alpha1", "4.05.0")
        assertIsVersion("~/.opam/4.05.0~alpha1+options", "4.05.0")
    }

    @Test
    fun testOCamlBaseCompiler1dThen2dThen2dThenKinds() {
        assertIsVersion("~/.opam/ocaml-base-compiler.4.05.0", "4.05.0")
        assertIsVersion("~/.opam/ocaml-base-compiler.4.05.0+flambda", "4.05.0")
        assertIsVersion("~/.opam/ocaml-base-compiler.4.05.0+flambda+options", "4.05.0")
        assertIsVersion("~/.opam/ocaml-base-compiler.4.05.0~alpha1", "4.05.0")
        assertIsVersion("~/.opam/ocaml-base-compiler.4.05.0~alpha1+options", "4.05.0")
    }

    @Test
    fun test1dThen2dThen2dThenFake() {
        assertIsUnknownVersion("~/.opam/4.12.0-v")
        assertIsUnknownVersion("~/.opam/4.12.0-v2")
    }
}
