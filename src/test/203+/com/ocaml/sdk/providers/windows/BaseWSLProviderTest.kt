package com.ocaml.sdk.providers.windows

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.wsl.WSLCommandLineOptions
import com.intellij.execution.wsl.WslDistributionManager
import com.intellij.openapi.util.SystemInfo
import com.ocaml.sdk.providers.BaseSdkProviderTest
import com.ocaml.sdk.providers.OCamlSdkProviderFolders
import com.ocaml.sdk.providers.OCamlTestSdkInfo
import com.ocaml.sdk.utils.OCamlSdkHomeUtils.isValid
import java.io.IOException

abstract class BaseWSLProviderTest : BaseSdkProviderTest() {
    private var _folders: WSLFolders? = null
    protected val folders: WSLFolders get() = _folders!!


    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        _folders = WSLFolders()
    }

    @Throws(Exception::class)
    override fun tearDown() {
        super.tearDown()
        _folders = null
    }

    protected fun assertWSLHomeValid(homePath: String?) {
        assertTrue(isValid(homePath!!))
    }

    protected fun assertWSLHomeInvalid(homePath: String?) {
        assertFalse(isValid(homePath!!))
    }
}

@Suppress("PropertyName")
class WSLFolders : OCamlSdkProviderFolders {
    private val isWSLCompatible: Boolean
        get() = SystemInfo.isWin10OrNewer

    override val name: String
        get() = "WSL"

    override val isBinAvailable: Boolean
        get() = BIN_CREATE_SDK != null || BIN_VALID_SDK != null

    override val isOpamAvailable: Boolean
        get() = OPAM_HOME != null || OPAM_VALID_SDK != null

    var BIN_VALID_SDK: OCamlTestSdkInfo? = null
    var BIN_CREATE_SDK: OCamlTestSdkInfo? = null
    var BIN_VALID: String? = null

    var OPAM_HOME: String? = null
    var OPAM_VALID_SDK: OCamlTestSdkInfo? = null
    var OPAM_INVALID_DIST: OCamlTestSdkInfo? = null
    var OPAM_INVALID: String? = null
    var OPAM_INVALID_BIN: String? = null

    var DUNE_INSTALLED: Boolean = false

    var OCAML_INVALID_SDK_HOME: String? = null
    var OCAML_BIN_INVALID: String? = null

    init {
        loadWSLSdks()
        //println(this)
    }

    private fun loadWSLSdks() {
        // not available
        if (!isWSLCompatible) return

        // get distributions
        val list = WslDistributionManager.getInstance().installedDistributions
        if (list.size <= 0) return

        // find
        for (distribution in list) {
            // opam, if installed
            try {
                var cli = GeneralCommandLine("opam", "switch", "show")
                cli = distribution.patchCommandLine(cli, null, WSLCommandLineOptions())
                var process = cli.createProcess()
                var version = String(process.inputStream.readAllBytes()).trim { it <= ' ' }
                if (version.isEmpty() || process.exitValue() != 0) continue

                /* path to the opam folder **/
                val opamFolder = distribution.safeUserHome() + "/.opam/"
                OPAM_HOME = distribution.getWindowsPath(opamFolder)

                /* everything should be valid */
                OPAM_VALID_SDK = OCamlTestSdkInfo(
                    OPAM_HOME + version, "$OPAM_HOME$version\\bin\\ocaml",
                    "", version, "\\lib\\ocaml"
                )

                OPAM_INVALID_DIST = OCamlTestSdkInfo(
                    "\\\\wsl$\\Fedora\\home\\username\\.opam\\4.07.0",
                    "\\\\wsl$\\Fedora\\home\\username\\.opam\\4.07.0\\bin\\ocaml",
                    "",
                    ""
                )

                /* expected: properly formatted path, non-existing SDK version */
                OPAM_INVALID = "$OPAM_HOME\\0.00.0"
                OPAM_INVALID_BIN = "$OPAM_HOME\\0.00.0\\bin\\ocaml"

                cli = GeneralCommandLine("dune", "--version")
                cli = distribution.patchCommandLine(cli, null, WSLCommandLineOptions())
                process = cli.createProcess()
                version = String(process.inputStream.readAllBytes()).trim { it <= ' ' }
                DUNE_INSTALLED = version.isNotEmpty() && process.exitValue() == 0
            } catch (ignore: ExecutionException) {
            } catch (ignore: IOException) {
            }

            // check native

            /* a valid file that is not ocaml **/
            BIN_VALID = distribution.getWindowsPath("/bin/find")

            try {
                var cli = GeneralCommandLine("/bin/ocamlc", "-version")
                cli = distribution.patchCommandLine(cli, null, WSLCommandLineOptions())
                var process = cli.createProcess()
                val version = String(process.inputStream.readAllBytes()).trim { it <= ' ' }
                if (version.isEmpty() || process.exitValue() != 0) continue

                cli = GeneralCommandLine("true")
                val options = WSLCommandLineOptions()
                options.addInitCommand("ocamlc -config | sed -nr 's/^standard_library: (.*)*/\\1/p'")
                cli = distribution.patchCommandLine(cli, null, options)
                process = cli.createProcess()
                val libFolder = String(process.inputStream.readAllBytes()).trim { it <= ' ' }
                if (libFolder.isEmpty() || process.exitValue() != 0) throw ExecutionException("No lib folder.")

                val root = distribution.getWindowsPath("/")
                val ocamlBin = distribution.getWindowsPath("/bin/ocaml")
                val ocamlCompilerBin = distribution.getWindowsPath("/bin/ocamlc")
                /* a valid binary candidate for an SDK **/
                BIN_VALID_SDK = OCamlTestSdkInfo(
                    root, ocamlBin,
                    "", version, libFolder.replace("/", "\\")
                )
                BIN_CREATE_SDK = OCamlTestSdkInfo(
                    null,
                    ocamlBin,
                    ocamlCompilerBin,
                    version,
                    distribution.getWindowsPath(libFolder)
                )
            } catch (ignore: ExecutionException) {
            } catch (ignore: IOException) {
            }
        }

        OCAML_INVALID_SDK_HOME = "\\\\wsl$\\Debian\\invalid"
        OCAML_BIN_INVALID = "\\\\wsl$\\Debian\\invalid\\ocaml"
    }

    override fun toString(): String {
        return "WSLFolders(BIN_VALID_SDK=$BIN_VALID_SDK, \n" +
                "BIN_CREATE_SDK=$BIN_CREATE_SDK, \n" +
                "BIN_VALID=$BIN_VALID, \n" +
                "OPAM_HOME=$OPAM_HOME, \n" +
                "OPAM_VALID_SDK=$OPAM_VALID_SDK, \n" +
                "OPAM_INVALID_DIST=$OPAM_INVALID_DIST, \n" +
                "OPAM_INVALID=$OPAM_INVALID, \n" +
                "OPAM_INVALID_BIN=$OPAM_INVALID_BIN, \n" +
                "HOME_INVALID=$OCAML_INVALID_SDK_HOME, \n" +
                "OCAML_BIN_INVALID=$OCAML_BIN_INVALID)\n"
    }


}