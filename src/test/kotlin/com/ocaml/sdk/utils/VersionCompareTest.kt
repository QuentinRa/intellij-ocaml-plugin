package com.ocaml.sdk.utils

import com.ocaml.OCamlBaseUtilityTest
import org.junit.Test

class VersionCompareTest : OCamlBaseUtilityTest() {
    private fun assertIsNewer(path1: String, path2: String, equals: Boolean) {
        // This function is an alias of compare, or is working the same
        // but, the base is the older version, and the version is the version
        // for which we are checking if this version is newer than the base
        assertTrue(OCamlSdkVersionUtils.isNewerThan(path2, path1))
        // if not equals, then the inverse is false
        if (!equals) assertFalse(OCamlSdkVersionUtils.isNewerThan(path1, path2))
        else assertTrue(OCamlSdkVersionUtils.isNewerThan(path1, path2))
    }

    private fun assertPath(path1: String, path2: String, expected: Int) {
        assertEquals(expected, OCamlSdkVersionUtils.comparePaths(path1, path2))
        if (path1.contains("/")) {
            // TR
            assertEquals(expected, OCamlSdkVersionUtils.comparePaths("$path1/", "$path2/"))
            // Windows
            assertEquals(
                expected,
                OCamlSdkVersionUtils.comparePaths(path1.replace("/", "\\"), path2.replace("/", "\\"))
            )
            // Windows + TR
            assertEquals(
                expected,
                OCamlSdkVersionUtils.comparePaths(path1.replace("/", "\\") + "\\", path2.replace("/", "\\") + "\\")
            )
        }
    }

    private fun assertSameVersion(path1: String, path2: String) {
        assertPath(path1, path2, 0)
        assertIsNewer(path1, path2, true)
    }

    @Test
    fun testSameNoPatch() {
        assertSameVersion("~/4.08", "~/4.08")
    }

    @Test
    fun testSame() {
        assertSameVersion("~/4.08.0", "~/4.08.0")
    }

    @Test
    fun testSameNoPatchFirst() {
        assertSameVersion("~/4.08", "~/4.08.0")
    }

    @Test
    fun testSameNoPatchSecond() {
        assertSameVersion("~/4.08.0", "~/4.08")
    }

    private fun assertIsLeftNewer(path1: String, path2: String) {
        assertPath(path1, path2, 1)
        // unless the world is going crazy
        // we can assume that
        // if "a" is newer "b"
        // then "b" is older "a"
        assertPath(path2, path1, -1)

        // Check isNewer too
        assertIsNewer(path1, path2, false)
    }

    @Test
    fun testGreaterNoPatch() {
        assertIsLeftNewer("~/4.12", "~/4.10")
    }

    @Test
    fun testGreater() {
        assertIsLeftNewer("~/4.12.0", "~/4.10.0")
        assertIsLeftNewer("~/4.12.0", "~/4.08.0")
        assertIsLeftNewer("~/4.12.1", "~/4.12.0")
    }

    @Test
    fun testGreaterFirstNoPatch() {
        assertIsLeftNewer("~/4.12", "~/4.10.0")
    }

    @Test
    fun testGreaterSecondNoPatch() {
        assertIsLeftNewer("~/4.12.0", "~/4.10")
    }

    @Test
    fun testGreaterKind() {
        assertIsLeftNewer("~/4.12.0+mingw64c", "~/4.10.0+mingw64c")
        assertIsLeftNewer("~/4.12.0+trunk+afl", "~/4.10.0+mingw64c")
        assertIsLeftNewer("~/4.12.0+trunk+afl", "~/4.10.0~alpha1")
        assertIsLeftNewer("~/4.12.0+mingw64c", "~/4.10.0+trunk+afl")
        assertIsLeftNewer("~/4.12.0+mingw64c", "~/4.10.0~alpha1")
        assertIsLeftNewer("~/4.12.0~alpha1", "~/4.10.0+mingw64c")
        assertIsLeftNewer("~/4.12.0~alpha1", "~/4.10.0+trunk+afl")
    }

    @Test
    fun testGreaterFirstKind() {
        assertIsLeftNewer("~/4.12.0+mingw64c", "~/4.10.0")
        assertIsLeftNewer("~/4.12.0+trunk+afl", "~/4.10.0")
        assertIsLeftNewer("~/4.12.0~alpha1", "~/4.10.0")
    }

    @Test
    fun testGreaterSecondKind() {
        assertIsLeftNewer("~/4.12.0", "~/4.10.0+mingw64c")
        assertIsLeftNewer("~/4.12.0", "~/4.10.0+trunk+afl")
        assertIsLeftNewer("~/4.12.0", "~/4.10.0~alpha1")
    }
}