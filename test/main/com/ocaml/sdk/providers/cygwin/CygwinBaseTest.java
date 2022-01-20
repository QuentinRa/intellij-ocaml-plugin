package com.ocaml.sdk.providers.cygwin;

import com.ocaml.OCamlBaseTest;
import com.ocaml.sdk.providers.simple.DetectionResult;
import com.ocaml.sdk.providers.simple.OCamlNativeDetector;

import java.nio.file.Path;

public class CygwinBaseTest extends OCamlBaseTest  {
    protected void assertCygwinValid(String ocamlBinary, String ocamlcName, String expectedVersion) {
        Path bin = Path.of(ocamlBinary).getParent();
        String root = bin.getParent().toFile().getAbsolutePath();
        String ocamlc = bin.resolve(ocamlcName).toFile().getAbsolutePath();
        DetectionResult detectionResult = OCamlNativeDetector.detectNativeSdk(ocamlBinary);
        assertEquals(ocamlBinary, detectionResult.ocaml);
        assertEquals(ocamlc, detectionResult.ocamlCompiler);
        assertEquals(root + "\\lib\\ocaml", detectionResult.sources);
        assertEquals(expectedVersion, detectionResult.version);
        assertFalse(detectionResult.isError);
    }

    protected void assertCygwinInvalid(String ocamlBin) {
        try {
            DetectionResult detectionResult = OCamlNativeDetector.detectNativeSdk(ocamlBin);
            if (detectionResult.isError) throw new AssertionError("OK");
        } catch (AssertionError e) {
            assertTrue(true);
            return;
        }
        fail("Supposed unreachable code");
    }

}