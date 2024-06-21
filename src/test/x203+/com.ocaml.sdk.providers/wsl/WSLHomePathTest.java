package com.ocaml.sdk.providers.wsl;

import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public final class WSLHomePathTest extends WSLBaseTest {

    @Test
    public void testFilesOkNoVersion() {
        // we may find every file, but the file name do not have the
        // version of ocaml (ex: 4.05.0), so it's not a valid homePath
        if (folders.BIN_VALID_SDK == null) return;
        assertWSLHomeInvalid(folders.BIN_VALID_SDK.path);
    }

    @Test
    public void testInvalid() {
        if (folders.HOME_INVALID == null) return;
        assertWSLHomeInvalid(folders.HOME_INVALID);
    }

    @Test
    public void testInvalidTS() {
        if (folders.HOME_INVALID == null) return;
        assertWSLHomeInvalid(folders.HOME_INVALID+"\\");
    }

    @Test
    public void testInvalidWSLDistribution() {
        if (folders.OPAM_INVALID_DIST == null) return;
        assertWSLHomeInvalid(folders.OPAM_INVALID_DIST.path);
    }

    @Test
    public void testInvalidWSLDistributionTS() {
        if (folders.OPAM_INVALID_DIST == null) return;
        assertWSLHomeInvalid(folders.OPAM_INVALID_DIST.path+"\\");
    }

    @Test
    public void testOpamValid() {
        if (folders.OPAM_VALID_SDK == null) return;
        assertWSLHomeValid(folders.OPAM_VALID_SDK.path);
    }

    @Test
    public void testOpamValidTS() {
        if (folders.OPAM_VALID_SDK == null) return;
        assertWSLHomeValid(folders.OPAM_VALID_SDK.path+"\\");
    }

    @Test
    public void testOpamInvalid() {
        if (folders.OPAM_INVALID == null) return;
        assertWSLHomeInvalid(folders.OPAM_INVALID);
    }

    @Test
    public void testOpamInvalidTS() {
        if (folders.OPAM_INVALID == null) return;
        assertWSLHomeInvalid(folders.OPAM_INVALID +"\\");
    }

}
