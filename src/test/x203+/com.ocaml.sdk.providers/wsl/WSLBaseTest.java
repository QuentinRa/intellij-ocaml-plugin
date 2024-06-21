package com.ocaml.sdk.providers.wsl;

import com.ocaml.sdk.providers.BaseSdkProviderTest;
import com.ocaml.sdk.utils.OCamlSdkHomeManager;

public class WSLBaseTest extends BaseSdkProviderTest {

    protected WSLFolders folders;

    @Override protected void setUp() throws Exception {
        super.setUp();
        folders = new WSLFolders();
    }

    @Override protected void tearDown() throws Exception {
        super.tearDown();
        folders = null;
    }

    protected void assertWSLHomeValid(String homePath) {
        assertTrue(OCamlSdkHomeManager.isValid(homePath));
    }

    protected void assertWSLHomeInvalid(String homePath) {
        assertFalse(OCamlSdkHomeManager.isValid(homePath));
    }

}
