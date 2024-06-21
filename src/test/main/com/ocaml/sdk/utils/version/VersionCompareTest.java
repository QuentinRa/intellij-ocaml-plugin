package com.ocaml.sdk.utils.version;

import com.ocaml.OCamlBaseTest;
import com.ocaml.sdk.utils.OCamlSdkVersionManager;
import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public final class VersionCompareTest extends OCamlBaseTest {

    private void assertIsNewer(String path1, String path2, boolean equals) {
        // This function is an alias of compare, or is working the same
        // but, the base is the older version, and the version is the version
        // for which we are checking if this version is newer than the base
        assertTrue(OCamlSdkVersionManager.isNewerThan(path2, path1));
        // if not equals, then the inverse is false
        if (!equals) assertFalse(OCamlSdkVersionManager.isNewerThan(path1, path2));
        else assertTrue(OCamlSdkVersionManager.isNewerThan(path1, path2));
    }

    private void assertPath(String path1, String path2, int expected) {
        assertEquals(expected, OCamlSdkVersionManager.comparePaths(path1, path2));
        if (path1.contains("/")) {
            // TR
            assertEquals(expected, OCamlSdkVersionManager.comparePaths(path1+"/", path2+"/"));
            // Windows
            assertEquals(expected, OCamlSdkVersionManager.comparePaths(path1.replace("/", "\\"), path2.replace("/", "\\")));
            // Windows + TR
            assertEquals(expected, OCamlSdkVersionManager.comparePaths(path1.replace("/", "\\")+"\\", path2.replace("/", "\\")+"\\"));
        }
    }

    private void assertSameVersion(String path1, String path2) {
        assertPath(path1, path2, 0);
        assertIsNewer(path1, path2, true);
    }

    @Test
    public void testSameNoPatch() {
        assertSameVersion("~/4.08", "~/4.08");
    }

    @Test
    public void testSame() {
        assertSameVersion("~/4.08.0", "~/4.08.0");
    }

    @Test
    public void testSameNoPatchFirst() {
        assertSameVersion("~/4.08", "~/4.08.0");
    }

    @Test
    public void testSameNoPatchSecond() {
        assertSameVersion("~/4.08.0", "~/4.08");
    }

    private void assertIsLeftNewer(String path1, String path2) {
        assertPath(path1, path2, 1);
        // unless the world is going crazy
        // we can assume that
        // if "a" is newer "b"
        // then "b" is older "a"
        assertPath(path2, path1, -1);

        // Check isNewer too
        assertIsNewer(path1, path2, false);
    }

    @Test
    public void testGreaterNoPatch() {
        assertIsLeftNewer("~/4.12", "~/4.10");
    }

    @Test
    public void testGreater() {
        assertIsLeftNewer("~/4.12.0", "~/4.10.0");
        assertIsLeftNewer("~/4.12.0", "~/4.08.0");
        assertIsLeftNewer("~/4.12.1", "~/4.12.0");
    }

    @Test
    public void testGreaterFirstNoPatch() {
        assertIsLeftNewer("~/4.12", "~/4.10.0");
    }

    @Test
    public void testGreaterSecondNoPatch() {
        assertIsLeftNewer("~/4.12.0", "~/4.10");
    }

    @Test
    public void testGreaterKind() {
        assertIsLeftNewer("~/4.12.0+mingw64c", "~/4.10.0+mingw64c");
        assertIsLeftNewer("~/4.12.0+trunk+afl", "~/4.10.0+mingw64c");
        assertIsLeftNewer("~/4.12.0+trunk+afl", "~/4.10.0~alpha1");
        assertIsLeftNewer("~/4.12.0+mingw64c", "~/4.10.0+trunk+afl");
        assertIsLeftNewer("~/4.12.0+mingw64c", "~/4.10.0~alpha1");
        assertIsLeftNewer("~/4.12.0~alpha1", "~/4.10.0+mingw64c");
        assertIsLeftNewer("~/4.12.0~alpha1", "~/4.10.0+trunk+afl");
    }

    @Test
    public void testGreaterFirstKind() {
        assertIsLeftNewer("~/4.12.0+mingw64c", "~/4.10.0");
        assertIsLeftNewer("~/4.12.0+trunk+afl", "~/4.10.0");
        assertIsLeftNewer("~/4.12.0~alpha1", "~/4.10.0");
    }

    @Test
    public void testGreaterSecondKind() {
        assertIsLeftNewer("~/4.12.0", "~/4.10.0+mingw64c");
        assertIsLeftNewer("~/4.12.0", "~/4.10.0+trunk+afl");
        assertIsLeftNewer("~/4.12.0", "~/4.10.0~alpha1");
    }

}