package com.ocaml.sdk.providers.linux;

import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public final class LinuxSuggestHomePathsTest extends LinuxBaseTest {

    @Test
    public void testCygwinOpamSdksAreSuggested() {
        if (folders.OPAM_HOME == null) return;
        assertInstallationFolderWasSuggested(folders.OPAM_HOME);
    }
}
