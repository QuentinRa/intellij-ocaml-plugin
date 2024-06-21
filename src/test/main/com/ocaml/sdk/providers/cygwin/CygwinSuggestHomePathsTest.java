package com.ocaml.sdk.providers.cygwin;

import org.junit.Test;

public final class CygwinSuggestHomePathsTest extends CygwinBaseTest {

    public CygwinSuggestHomePathsTest(String folderName) {
        super(folderName);
    }

    @Test
    public void testCygwinOpamSdksAreSuggested() {
        if (folders.OPAM_HOME == null) return;
        if (folders.isCI()) return;
        assertInstallationFolderWasSuggested(folders.OPAM_HOME);
    }
}
