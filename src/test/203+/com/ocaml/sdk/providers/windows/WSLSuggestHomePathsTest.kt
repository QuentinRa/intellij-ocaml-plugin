package com.ocaml.sdk.providers.windows

import org.junit.Test

class WSLSuggestHomePathsTest : BaseWSLProviderTest() {
    @Test
    fun testCygwinOpamSdksAreSuggested() {
        folders.OPAM_HOME?.let { assertInstallationFolderWasSuggested(it) }
    }
}
