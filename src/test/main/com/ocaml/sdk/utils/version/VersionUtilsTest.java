package com.ocaml.sdk.utils.version;

import com.ocaml.OCamlBaseTest;
import com.ocaml.sdk.utils.OCamlSdkVersionManager;
import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public final class VersionUtilsTest extends OCamlBaseTest {

    @Test
    public void testIsUnknown() {
        assertTrue(OCamlSdkVersionManager.isUnknownVersion(OCamlSdkVersionManager.UNKNOWN_VERSION));
    }

    @Test
    public void testIsNotUnknown() {
        assertFalse(OCamlSdkVersionManager.isUnknownVersion("4.13.0"));
    }

}
