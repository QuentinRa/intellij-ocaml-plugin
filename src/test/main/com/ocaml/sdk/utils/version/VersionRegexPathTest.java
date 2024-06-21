package com.ocaml.sdk.utils.version;

import com.ocaml.OCamlBaseTest;
import com.ocaml.sdk.utils.OCamlSdkVersionManager;
import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public final class VersionRegexPathTest extends OCamlBaseTest {

    private void assertIsUnknownVersion(String path) {
        assertTrue(OCamlSdkVersionManager.isUnknownVersion(OCamlSdkVersionManager.parse(path)));
    }

    private void assertIsVersion(String path, String expectedVersion) {
        assertEquals(expectedVersion, OCamlSdkVersionManager.parse(path));
        if (expectedVersion.contains("/")) {
            // TR
            assertEquals(expectedVersion, OCamlSdkVersionManager.parse(path+"/"));
            // Windows
            assertEquals(expectedVersion, OCamlSdkVersionManager.parse(path.replace("/", "\\")));
            // Windows + TR
            assertEquals(expectedVersion, OCamlSdkVersionManager.parse(path.replace("/", "\\")+"\\"));
        }
    }

    @Test
    public void testEmpty() {
        assertIsUnknownVersion("");
    }

    @Test
    public void testNotAPath() {
        assertIsUnknownVersion("4.05.0");
    }

    @Test
    public void test2d() {
        assertIsUnknownVersion("/40.5");
        assertIsUnknownVersion("/40.50");
    }

    @Test
    public void test1dThen2d() {
        assertIsVersion("~/.opam/4.05", "4.05");
    }

    @Test
    public void test1dThen2dThenEmpty() {
        assertIsUnknownVersion("~/.opam/4.05.");
    }

    @Test
    public void test1dThen2dThen1d() {
        assertIsVersion("~/.opam/4.05.2", "4.05.2");
    }

    @Test
    public void test1dThen2dThen2d() {
        assertIsUnknownVersion("~/.opam/4.05.02");
    }

    @Test
    public void test1dThen2dThen2dThenMingw() {
        assertIsVersion("~/.opam/4.13.1+mingw64c", "4.13.1+mingw64c");
    }

    @Test
    public void test1dThen2dThen2dThenKinds() {
        assertIsVersion("~/.opam/4.13.1+trunk+afl", "4.13.1+trunk+afl");
        assertIsVersion("~/.opam/4.13.1+musl+static+flambda", "4.13.1+musl+static+flambda");
        assertIsVersion("~/.opam/4.13.1+flambda+no-float-float-array", "4.13.1+flambda+no-float-float-array");
        assertIsVersion("~/.opam/4.05.0~alpha1", "4.05.0~alpha1");
        assertIsVersion("~/.opam/4.05.0~alpha1+options", "4.05.0~alpha1+options");
    }

    @Test
    public void testOCamlBaseCompiler1dThen2dThen2dThenKinds() {
        assertIsVersion("~/.opam/ocaml-base-compiler.4.05.0", "4.05.0");
        assertIsVersion("~/.opam/ocaml-base-compiler.4.05.0+flambda", "4.05.0+flambda");
        assertIsVersion("~/.opam/ocaml-base-compiler.4.05.0+flambda+options", "4.05.0+flambda+options");
        assertIsVersion("~/.opam/ocaml-base-compiler.4.05.0~alpha1", "4.05.0~alpha1");
        assertIsVersion("~/.opam/ocaml-base-compiler.4.05.0~alpha1+options", "4.05.0~alpha1+options");
    }

    @Test
    public void test1dThen2dThen2dThenFake() {
        assertIsUnknownVersion("~/.opam/4.12.0-v");
        assertIsUnknownVersion("~/.opam/4.12.0-v2");
        assertIsUnknownVersion("~/.opam/4.12.0-v/");
        assertIsUnknownVersion("~/.opam/4.12.0-v2/");
        assertIsUnknownVersion("~\\.opam\\4.12.0-v");
        assertIsUnknownVersion("~\\.opam\\4.12.0-v2");
        assertIsUnknownVersion("~\\.opam\\4.12.0-v\\");
        assertIsUnknownVersion("~\\.opam\\4.12.0-v2\\");
    }
}
