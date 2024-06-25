package com.ocaml.sdk.providers.windows

import com.ocaml.sdk.providers.OCamlSdkProviderDune
import com.ocaml.sdk.providers.OCamlSdkProvidersManager
import org.junit.Assume.assumeTrue
import org.junit.Test

class WslDuneTest : BaseWSLProviderTest() {

    @Test
    fun test_dune_version() {
        assumeTrue(folders.DUNE_INSTALLED);
        folders.OPAM_VALID_SDK?.let { assertNotNull(OCamlSdkProvidersManager.getDuneVersion(it.path)) }
    }

    private fun runDuneExecTest(): List<String> {
        assumeTrue(folders.DUNE_INSTALLED);
        folders.OPAM_VALID_SDK?.let {
            val directory = myFixture.testDataPath
            val targetName = "dummy"
            val res = OCamlSdkProvidersManager.getDuneExecCommand(it.path!!, directory, targetName, directory, directory, mutableMapOf())
            assertNotNull(res) ; res!!
            val parts = res.commandLineString.split(' ')
            assertContainsElements(parts,
                // the command
                "exec",
                // the target
                OCamlSdkProviderDune.computeTargetName("", "", targetName)
            )
            return parts
        }
        return listOf()
    }

    @Test
    fun test_dune_exec() {
        runDuneExecTest()
    }
}