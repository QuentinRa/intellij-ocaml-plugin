package com.ocaml.sdk.utils.version

import com.intellij.testFramework.UsefulTestCase
import com.ocaml.sdk.utils.OCamlSdkVersionUtils
import org.junit.Test

@Suppress("JUnitMixedFramework")
class VersionUnknownTest : UsefulTestCase() {
    @Test
    fun testIsUnknown() {
        assertTrue(OCamlSdkVersionUtils.isUnknownVersion(OCamlSdkVersionUtils.UNKNOWN_VERSION))
    }

    @Test
    fun testIsNotUnknown() {
        assertFalse(OCamlSdkVersionUtils.isUnknownVersion("4.13.0"))
    }
}