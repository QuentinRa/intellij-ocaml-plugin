package com.ocaml.sdk.utils.version;

import com.ocaml.OCamlBaseTest;
import com.ocaml.sdk.utils.OCamlSdkVersionManager;
import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public final class VersionRegexTest extends OCamlBaseTest {

    // invalid

    @Test
    public void test1d() {
        assertFalse(OCamlSdkVersionManager.isValid("4"));
    }

    @Test
    public void test1dEmpty() {
        assertFalse(OCamlSdkVersionManager.isValid("4."));
    }

    @Test
    public void test1d1d() {
        assertFalse(OCamlSdkVersionManager.isValid("4.0"));
    }

    @Test
    public void test1d1dEmpty() {
        assertFalse(OCamlSdkVersionManager.isValid("4.0."));
    }

    @Test
    public void test1d1d1d() {
        assertFalse(OCamlSdkVersionManager.isValid("4.0.0"));
    }

    @Test
    public void testInvalidStart() {
        // now, this is valid :D
        assertTrue(OCamlSdkVersionManager.isValid("ocaml-4.05.0"));
    }

    // valid

    @Test
    public void test1d2d() {
        assertTrue(OCamlSdkVersionManager.isValid("4.05"));
    }

    @Test
    public void test1d2dKind() {
        assertTrue(OCamlSdkVersionManager.isValid("4.05+mingw64c"));
    }

    @Test
    public void test1d2d1d() {
        assertTrue(OCamlSdkVersionManager.isValid("4.05.0"));
    }

    @Test
    public void test1d2d1dKind() {
        assertTrue(OCamlSdkVersionManager.isValid("4.05.0+mingw64c"));
    }

    @Test
    public void test1d2d1d2Kinds() {
        assertTrue(OCamlSdkVersionManager.isValid("4.05.0+trunk+afl"));
    }

    @Test
    public void test1d2d1d3Kinds() {
        assertTrue(OCamlSdkVersionManager.isValid("4.05.0+musl+static+flambda"));
    }

    @Test
    public void test1d2d1dKindsAndMultiWord() {
        assertTrue(OCamlSdkVersionManager.isValid("4.05.0+flambda+no-float-float-array"));
    }

    @Test
    public void test1d2d1dAlpha() {
        assertTrue(OCamlSdkVersionManager.isValid("4.05.0~alpha1"));
    }

    @Test
    public void test1d2d1dAlphaKind() {
        assertTrue(OCamlSdkVersionManager.isValid("4.05.0+0~alpha1+options"));
    }

    @Test
    public void test1d2d1dSpecial() {
        assertFalse(OCamlSdkVersionManager.isValid("4.05.0+flambda+no float float array"));
        assertFalse(OCamlSdkVersionManager.isValid("4.05.0+flambda+*"));
        assertFalse(OCamlSdkVersionManager.isValid("4.05.0+flambda+/"));
        assertFalse(OCamlSdkVersionManager.isValid("4.05.0+flambda+%"));
        assertFalse(OCamlSdkVersionManager.isValid("4.05.0+flambda+_"));
        assertFalse(OCamlSdkVersionManager.isValid("4.05.0+flambda+@"));
        assertFalse(OCamlSdkVersionManager.isValid("4.05.0+flambda+NOT"));
        assertFalse(OCamlSdkVersionManager.isValid("4.05.0+flambda++"));
    }

    @Test
    public void test1d2d1dOCamlBaseCompiler() {
        assertTrue(OCamlSdkVersionManager.isValid("ocaml-base-compiler.4.05.0"));
        assertTrue(OCamlSdkVersionManager.isValid("ocaml-base-compiler.4.05.0+flambda"));
        assertTrue(OCamlSdkVersionManager.isValid("ocaml-base-compiler.4.05.0+flambda+options"));
        assertTrue(OCamlSdkVersionManager.isValid("ocaml-base-compiler.4.05.0~alpha1"));
        assertTrue(OCamlSdkVersionManager.isValid("ocaml-base-compiler.4.05.0~alpha1+options"));
    }

    @Test
    public void testTricky() {
        // :D
        assertFalse(OCamlSdkVersionManager.isValid("4.05.0.4.05.0"));
    }

    @Test
    public void test1d2d1dFake() {
        assertFalse(OCamlSdkVersionManager.isValid("4.05.0-v"));
        assertFalse(OCamlSdkVersionManager.isValid("4.05.0-v2"));
        assertFalse(OCamlSdkVersionManager.isValid("4.05.0-"));
    }
}
