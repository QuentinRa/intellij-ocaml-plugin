package com.ocaml.sdk.providers.linux;

import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public final class LinuxHomePathTest extends LinuxBaseTest {

    @Test
    public void testFilesOkNoVersion() {
        if (folders.BIN_VALID_SDK == null) return;
        // we may find every file, but the file name do not have the
        // version of ocaml (ex: 4.05.0), so it's not a valid homePath
        assertWSLHomeInvalid(folders.BIN_VALID_SDK.path);
    }

    @Test
    public void testInvalid() {
        if (folders.BIN_INVALID == null) return;
        assertWSLHomeInvalid(folders.BIN_INVALID);
    }

    @Test
    public void testInvalidTS() {
        if (folders.BIN_INVALID == null) return;
        assertWSLHomeInvalid(folders.BIN_INVALID + "/");
    }

    @Test
    public void testOpamValid() {
        if (folders.OPAM_VALID_SDK == null) return;
        assertWSLHomeValid(folders.OPAM_VALID_SDK.path);
    }

    @Test
    public void testOpamValidTS() {
        if (folders.OPAM_VALID_SDK == null) return;
        assertWSLHomeValid(folders.OPAM_VALID_SDK.path+"/");
    }

    @Test
    public void testOpamInvalid() {
        if (folders.OPAM_INVALID == null) return;
        assertWSLHomeInvalid(folders.OPAM_INVALID);
    }

    @Test
    public void testOpamInvalidTS() {
        if (folders.OPAM_INVALID == null) return;
        assertWSLHomeInvalid(folders.OPAM_INVALID +"/");
    }

}
