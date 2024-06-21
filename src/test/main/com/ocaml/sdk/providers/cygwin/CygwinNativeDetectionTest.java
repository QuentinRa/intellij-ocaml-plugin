package com.ocaml.sdk.providers.cygwin;

import org.junit.Test;

public final class CygwinNativeDetectionTest extends CygwinBaseTest {
    public CygwinNativeDetectionTest(String folderName) {
        super(folderName);
    }

    //
    // Testing
    //

    @Test
    public void testEmpty() {
        assertCygwinDetectionInvalid("");
    }

    @Test
    public void testPathInvalid() {
        if (folders.OCAML_BIN_INVALID == null) return;
        assertCygwinDetectionInvalid(folders.OCAML_BIN_INVALID);
    }

    @Test
    public void testNoExe() {
        if (folders.OCAML_BIN_INVALID_NO_EXE == null) return;
        assertCygwinDetectionInvalid(folders.OCAML_BIN_INVALID_NO_EXE);
    }

    @Test
    public void testNotOCaml() {
        if (folders.BIN_VALID_EXE == null) return;
        assertCygwinDetectionInvalid(folders.BIN_VALID_EXE);
    }

    @Test
    public void testBin() {
        if (folders.BIN_VALID_SDK == null) return;
        assertCygwinDetectionValid(folders.BIN_VALID_SDK);
    }

    @Test
    public void testOpamBinValid() {
        if (folders.OPAM_VALID_SDK == null) return;
        if (folders.isCI()) return;
        assertCygwinDetectionValid(folders.OPAM_VALID_SDK);
    }

    @Test
    public void testOpamBinInvalid() {
        if (folders.OPAM_INVALID_BIN == null) return;
        assertCygwinDetectionInvalid(folders.OPAM_INVALID_BIN);
    }
}
