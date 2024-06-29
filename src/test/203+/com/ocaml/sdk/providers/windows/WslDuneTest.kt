package com.ocaml.sdk.providers.windows

import com.dune.sdk.api.DuneCommand
import com.dune.sdk.api.DuneCommandParameters
import com.dune.sdk.utils.DuneSdkUtils
import com.ocaml.sdk.providers.OCamlSdkProvidersManager
import org.junit.Assume.assumeTrue
import org.junit.Test

class WslDuneTest : BaseWSLProviderTest() {

    @Test
    fun testDuneVersion() {
        assumeTrue(folders.DUNE_INSTALLED);
        folders.OPAM_VALID_SDK?.let { assertNotNull(OCamlSdkProvidersManager.getDuneVersion(it.path)) }
    }

    @Test
    fun testDuneVersionInvalid() {
        folders.OPAM_INVALID_DIST?.let { assertNull(OCamlSdkProvidersManager.getDuneVersion(it.path)) }
    }

    private fun runDuneExecTest(cmdArgs: String="", executableArgs: String=""): List<String>? {
        assumeTrue(folders.DUNE_INSTALLED);
        folders.OPAM_VALID_SDK?.let {
            val directory = myFixture.testDataPath
            val targetName = "dummy"
            val args = DuneCommandParameters(DuneCommand.EXEC, directory, targetName, directory, directory,
                cmdArgs, executableArgs, mutableMapOf())
            val res = OCamlSdkProvidersManager.prepareDuneCommand(it.path!!, args)
            assertNotNull(res) ; res!!
            val parts = res.commandLineString.split(' ')
            assertContainsElements(parts,
                // the command
                DuneCommand.EXEC.value,
                // the target
                DuneSdkUtils.computeTargetName("", "", targetName)
            )
            return parts
        }
        return null
    }

    @Test
    fun testDuneExecInvalid() {
        folders.OPAM_INVALID_DIST?.let { assertNull(OCamlSdkProvidersManager.prepareDuneCommand(it.path!!,
            DuneCommandParameters(DuneCommand.EXEC,"", "", "", "", "", "", mutableMapOf())))
        }
    }

    @Test
    fun testDuneExec() {
        runDuneExecTest()
    }

    @Test
    fun testDuneExecCommandArgs() {
        val parts = runDuneExecTest("--build-info") ?: return
        assertContainsElements(parts, "--build-info")
    }

    @Test
    fun testDuneExecExecutableArgs() {
        val parts = runDuneExecTest("", "\"toto\" \"titi\"") ?: return
        assertContainsElements(parts, "toto", "titi")
    }

    @Test
    fun testDuneExecArgs() {
        val parts = runDuneExecTest("--build-info", "\"toto\" \"tata\"") ?: return
        assertContainsElements(parts, "--build-info", "toto", "tata")
    }

    @Test
    fun testDuneExecExecutableArgsWithSpace() {
        val parts = runDuneExecTest("", "\"toto\" tata \"titi\"") ?: return
        assertContainsElements(parts, "toto", "tata", "titi")
    }
}