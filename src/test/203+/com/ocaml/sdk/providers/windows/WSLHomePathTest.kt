package com.ocaml.sdk.providers.windows

import org.junit.Test

class WSLHomePathTest : WSLBaseTest() {

    @Test
    fun testFilesOkNoVersion() {
        // we may find every file, but the file name do not have the
        // version of ocaml (ex: 4.05.0), so it's not a valid homePath
        folders.BIN_VALID_SDK?.let { assertWSLHomeInvalid(it.path)  }
    }

    @Test
    fun testInvalid() {
        folders.HOME_INVALID?.let { assertWSLHomeInvalid(it)  }
    }

    @Test
    fun testInvalidTS() {
        folders.HOME_INVALID?.let { assertWSLHomeInvalid(it + "\\")  }
    }

    @Test
    fun testInvalidWSLDistribution() {
        folders.OPAM_INVALID_DIST?.let { assertWSLHomeInvalid(it.path)  }
    }

    @Test
    fun testInvalidWSLDistributionTS() {
        folders.OPAM_INVALID_DIST?.let { assertWSLHomeInvalid(it.path + "\\")  }
    }

    @Test
    fun testOpamValid() {
        folders.OPAM_VALID_SDK?.let { assertWSLHomeValid(it.path)  }
    }

    @Test
    fun testOpamValidTS() {
        folders.OPAM_VALID_SDK?.let { assertWSLHomeValid(it.path + "\\")  }
    }

    @Test
    fun testOpamInvalid() {
        folders.OPAM_INVALID?.let { assertWSLHomeInvalid(it)  }
    }

    @Test
    fun testOpamInvalidTS() {
        folders.OPAM_INVALID?.let { assertWSLHomeInvalid(it + "\\")  }
    }
}
