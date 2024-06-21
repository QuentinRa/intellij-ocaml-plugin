package com.ocaml.sdk.providers.wsl;

import com.ocaml.sdk.providers.simple.DetectionResult;
import com.ocaml.sdk.providers.simple.OCamlNativeDetector;
import com.ocaml.sdk.utils.SdkInfo;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public final class WSLNativeDetectionTest extends WSLBaseTest {

    public void assertWSLValid(@NotNull SdkInfo info) {
        assertWSLValid(info.toplevel, info.version, info.sources);
    }

    public void assertWSLValid(@NotNull String ocamlBin, String expectedVersion, String expectedLib) {
        String root = ocamlBin.replace("\\bin\\ocaml", "");
        DetectionResult detectionResult = OCamlNativeDetector.detectNativeSdk(ocamlBin);
        assertEquals(ocamlBin, detectionResult.ocaml);
        assertEquals(ocamlBin+"c", detectionResult.ocamlCompiler);
        assertEquals(root + expectedLib, detectionResult.sources);
        assertEquals(expectedVersion, detectionResult.version);
        assertFalse(detectionResult.isError);
    }

    public void assertWSLInvalid(String ocamlBin) {
        try {
            DetectionResult detectionResult = OCamlNativeDetector.detectNativeSdk(ocamlBin);
            if (detectionResult.isError) throw new AssertionError("OK");
        } catch (AssertionError e) {
            assertTrue(true);
            return;
        }
        fail("Supposed unreachable code");
    }

    @Test
    public void testEmpty() {
        if (folders.OCAML_BIN_INVALID == null) return;
        assertWSLInvalid("");
    }

    @Test
    public void testPathInvalid() {
        if (folders.OCAML_BIN_INVALID == null) return;
        assertWSLInvalid(folders.OCAML_BIN_INVALID);
    }

    @Test
    public void testInvalidWSLDistribution() {
        if (folders.OPAM_INVALID_DIST == null) return;
        assertWSLInvalid(folders.OPAM_INVALID_DIST.toplevel);
    }

    @Test
    public void testNotOCaml() {
        if (folders.BIN_VALID == null) return;
        assertWSLInvalid(folders.BIN_VALID);
    }

    @Test
    public void testBin() {
        if (folders.BIN_VALID_SDK == null) return;
        assertWSLValid(folders.BIN_VALID_SDK);
    }

    @Test
    public void testOpamBinValid() {
        if (folders.OPAM_VALID_SDK == null) return;
        assertWSLValid(folders.OPAM_VALID_SDK);
    }

    @Test
    public void testOpamBinInvalid() {
        if (folders.OPAM_INVALID_BIN == null) return;
        assertWSLInvalid(folders.OPAM_INVALID_BIN);
    }
}
