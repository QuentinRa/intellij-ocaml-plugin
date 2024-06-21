package com.ocaml.sdk.utils

import com.ocaml.OCamlBaseUtilityTest
import org.junit.Test

class VersionUnknownTest : OCamlBaseUtilityTest() {
    @Test
    fun testIsUnknown() {
        assertTrue(OCamlSdkVersionUtils.isUnknownVersion(OCamlSdkVersionUtils.UNKNOWN_VERSION))
    }

    @Test
    fun testIsNotUnknown() {
        assertFalse(OCamlSdkVersionUtils.isUnknownVersion("4.13.0"))
    }
}