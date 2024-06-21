package com.ocaml.sdk.providers.cygwin;

import org.junit.Test;

public final class CygwinHomePathTest extends CygwinBaseTest  {

    public CygwinHomePathTest(String folderName) {
        super(folderName);
    }

    @Test
    public void testFilesOkNoVersion() {
        if (folders.BIN_VALID_SDK == null) return;
        // we got every file, but the file name do not have the
        // version of ocaml (ex: 4.05.0), so it's not a valid homePath
        assertCygwinHomeInvalid(folders.BIN_VALID_SDK.path);
    }

    @Test
    public void testInvalid() {
        if (folders.HOME_INVALID == null) return;
        assertCygwinHomeInvalid(folders.HOME_INVALID);
    }

    @Test
    public void testInvalidTs() {
        if (folders.HOME_INVALID == null) return;
        assertCygwinHomeInvalid(folders.HOME_INVALID+"\\");
    }

    @Test
    public void testOpamValid() {
        if (folders.OPAM_VALID_SDK == null) return;
        assertCygwinHomeValid(folders.OPAM_VALID_SDK.path);
    }

    @Test
    public void testOpamInvalid() {
        if (folders.OPAM_INVALID_BIN == null) return;
        assertCygwinHomeInvalid(folders.OPAM_INVALID_BIN);
    }

}
