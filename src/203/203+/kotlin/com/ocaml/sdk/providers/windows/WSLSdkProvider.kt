package com.ocaml.sdk.providers.windows

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.wsl.*
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.impl.wsl.WslConstants
import com.ocaml.sdk.providers.AssociatedBinaries
import com.ocaml.sdk.providers.CompileWithCmtInfo
import com.ocaml.sdk.providers.InvalidHomeError
import com.ocaml.sdk.providers.unix.UnixOCamlSdkProvider
import com.ocaml.sdk.utils.OCamlSdkVersionUtils
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

/**
 * A WSL can be installed easily with "Windows Store > Debian" or
 * "Windows Store > Ubuntu". They are working like a "Linux on Windows".
 * Everything involving WSL is complex, because the paths must be converted
 * again and again, and we have to look for installed distributions sometimes (slow).
 */
class WSLSdkProvider : UnixOCamlSdkProvider() {
    override fun canUseProviderForOCamlBinary(path: String): Boolean = false

    override val installationFolders: Set<String>
        get() {
            val homePaths: MutableSet<String> = HashSet()
            // WSL
            for (distro: WSLDistribution in WslDistributionManager.getInstance().installedDistributions) {
                val wslPath = distro.userHome ?: continue
                val windowsPath = distro.getWindowsPath("$wslPath/.opam/")
                homePaths.add(windowsPath)
                // we may have Simple SDKs
                //fixme: homePaths.add(distro.getWindowsPath(expandUserHome(distro, SimpleSdkData.SDK_FOLDER)))
            }
            return homePaths
        }

    override fun createSdkFromBinaries(
        ocaml: String, compiler: String,
        version: String, sources: String,
        sdkFolder: String, sdkModifier: String
    ): String? {
        // is wsl
        var ocaml: String? = ocaml
        var compiler: String? = compiler
        var sources: String? = sources
        var sdkFolder = sdkFolder
        val path = WslPath.parseWindowsUncPath((ocaml)!!) ?: return null
        sdkFolder += "/$version$sdkModifier"

        val distribution = path.distribution
        ocaml = distribution.getWslPath(ocaml)
        if (ocaml == null) return null
        compiler = distribution.getWslPath(compiler!!)
        if (compiler == null) return null
        sources = distribution.getWslPath(sources!!)
        if (sources == null) return null

        // the order will be reversed, so we need to put the last commands first
        val wslCommandLineOptions = WSLCommandLineOptions()
        // get absolute path to the created SDK folder (with the $i)
        wslCommandLineOptions.addInitCommand("find $sdkFolder\$i -maxdepth 0 2>/dev/null")
        // link to sources
        wslCommandLineOptions.addInitCommand("ln -s $sources $sdkFolder\$i/lib/")
        // link to compiler
        wslCommandLineOptions.addInitCommand("ln -s $compiler $sdkFolder\$i/bin/ocamlc")
        // link to ocaml
        wslCommandLineOptions.addInitCommand("ln -s $ocaml $sdkFolder\$i/bin/ocaml")
        // create lib
        wslCommandLineOptions.addInitCommand("mkdir -p $sdkFolder\$i/lib/")
        // create both SDK folder and bin
        wslCommandLineOptions.addInitCommand("mkdir -p $sdkFolder\$i/bin")
        // find $i
        wslCommandLineOptions.addInitCommand("i=0; while true; do if [ ! -d $sdkFolder\$i ]; then break; else i=$((i+1)); fi done")

        try {
            val cli = distribution.patchCommandLine(GeneralCommandLine("true"), null, wslCommandLineOptions)
            LOG.debug("The CLI was: " + cli.commandLineString)
            val process = cli.createProcess()
            // parse the result of find
            var out = String(process.inputStream.readAllBytes())
            out = out.replace("\n", "")
            return distribution.getWindowsPath(out).trim { it <= ' ' }
        } catch (e: ExecutionException) {
            LOG.error("Couldn't process command. Error:" + e.message)
            return null
        } catch (e: IOException) {
            LOG.error("Couldn't process command. Error:" + e.message)
            return null
        }
    }

    /**
     * It's worth noting that we could use the one in the BaseProvider, but
     * "Files.exists" is failing for every symbolic link, and I don't want that.
     */
    override fun getAssociatedBinaries(ocamlBinary: String): AssociatedBinaries? {
        // is ocaml
        if (!ocamlBinary.endsWith("ocaml")) return null
        // is wsl
        val path = WslPath.parseWindowsUncPath(ocamlBinary) ?: return null
        // OK let's start
        LOG.debug("Detected WSL " + path.distribution + " for " + ocamlBinary)
        val distribution = path.distribution
        // get path to ocamlc
        val ocamlc = distribution.getWslPath(ocamlBinary + "c")
        if (ocamlc == null) {
            LOG.debug("ocamlc not found for $ocamlBinary")
            return null
        }
        // get sources
        val root = ocamlc.replace("bin/ocamlc", "")
        var sourcesFolder: String? = null
        for (s: String in oCamlSourcesFolders) {
            val sourcePath = root + s
            // try to convert to WSL path
            val sourceCandidate = distribution.getWindowsPath(sourcePath)
            // Exists?
            if (!Files.exists(Path.of(sourceCandidate))) continue
            // OK
            sourcesFolder = sourceCandidate
            break
        }
        if (sourcesFolder == null) {
            LOG.debug("No sources folder")
            return null
        }

        // ocamlc -version
        try {
            var cli = GeneralCommandLine(ocamlc, "-version")
            cli = distribution.patchCommandLine(cli, null, WSLCommandLineOptions())
            LOG.debug("CLI is: " + cli.commandLineString)
            val process = cli.createProcess()
            val inputStream = process.inputStream
            var version = String(inputStream.readAllBytes()).trim { it <= ' ' }
            LOG.debug("Version of $ocamlc is '$version'.")
            // if we got something better (ex: 4.05.0+mingw64, or 4.05.0+local)
            val alt: String = OCamlSdkVersionUtils.parse(ocamlBinary)
            if (!OCamlSdkVersionUtils.isUnknownVersion(alt)) version = alt
            return AssociatedBinaries(ocamlBinary, ocamlBinary + "c", sourcesFolder, version)
        } catch (e: ExecutionException) {
            LOG.debug(e.message)
        } catch (e: IOException) {
            LOG.debug(e.message)
        }
        return null
    }

    override fun getCompilerVersionCLI(ocamlcCompilerPath: String?): GeneralCommandLine? {
        return null
    }

    override fun getREPLCommand(sdkHomePath: String?): GeneralCommandLine? {
        // is wsl
        val path = WslPath.parseWindowsUncPath((sdkHomePath)!!) ?: return null
        try {
            val ocaml = path.linuxPath + "/bin/ocaml"
            val cli = GeneralCommandLine(ocaml, "-no-version")
            return path.distribution.patchCommandLine(cli, null, WSLCommandLineOptions())
        } catch (e: ExecutionException) {
            LOG.error("Error creating REPL command", e)
            return null
        }
    }

    override fun getCompileCommandWithCmt(
        sdkHomePath: String?,
        rootFolderForTempering: String?,
        file: String?,
        outputDirectory: String?,
        executableName: String?
    ): CompileWithCmtInfo? {
        // is wsl
        val path = WslPath.parseWindowsUncPath((sdkHomePath)!!) ?: return null
        try {
            val distribution = path.distribution
            val wslOutputDirectory = distribution.getWslPath((outputDirectory)!!)
                ?: throw ExecutionException("Could not parse output directory:$outputDirectory")
            val wslFile = distribution.getWslPath((file)!!)
                ?: throw ExecutionException("Could not parse file:$file")

            // create cli
            var cli: GeneralCommandLine = CompileWithCmtInfo.createAnnotatorCommand(
                path.linuxPath + "/bin/ocamlc",
                wslFile, "$wslOutputDirectory/$executableName",
                wslOutputDirectory, outputDirectory /* use OS working directory */
            )
            cli = distribution.patchCommandLine(cli, null, WSLCommandLineOptions())
            val wslRootFolderForTempering = distribution.getWslPath((rootFolderForTempering)!!)
                ?: throw ExecutionException("Could not parse rootFolder:$rootFolderForTempering")
            return CompileWithCmtInfo(cli, wslRootFolderForTempering)
        } catch (e: ExecutionException) {
            LOG.error("Error creating Compiler command", e)
            return null
        }
    }

    override val oCamlCompilerCommands: List<String>
        //
        get() = listOf("ocamlc", "ocamlc.opt") // because ocamlc is not valid on Windows

    override fun canUseProviderForHome(homePath: Path): Boolean {
        if (!WSLUtil.isSystemCompatible()) return false
        val windowsUncPath = homePath.toFile().absolutePath
        var path = FileUtil.toSystemDependentName(windowsUncPath)
        // fixme: hardcoded new WSL prefix
        if (path.startsWith(WslConstants.UNC_PREFIX)) path = StringUtil.trimStart(path, WslConstants.UNC_PREFIX)
        else if (path.startsWith("\\\\wsl.localhost\\")) path = StringUtil.trimStart(path, "\\\\wsl.localhost\\")
        else return false
        val index = path.indexOf('\\')
        return index > 0
    }

    override fun handleSymlinkHomePath(homePath: Path): InvalidHomeError? {
        val path = WslPath.parseWindowsUncPath(homePath.toFile().absolutePath) ?: return null
        val distribution = path.distribution

        // they are the ONLY path allowed in an SDK, by definition
        // other paths that were allowed in other places, are not directly
        // used with SDK, they will be renamed, etc., so that they match
        // the SDK expected file structure
        var ocaml = homePath.resolve("bin/ocaml").toFile().absolutePath
        var compiler = homePath.resolve("bin/ocamlc").toFile().absolutePath
        var sources = homePath.resolve("lib/ocaml/").toFile().absolutePath

        ocaml = distribution.getWslPath(ocaml)
        if (ocaml == null) return null
        compiler = distribution.getWslPath(compiler)
        if (compiler == null) return null
        sources = distribution.getWslPath(sources)
        if (sources == null) return null

        var cli = GeneralCommandLine("true")
        val wslCommandLineOptions = WSLCommandLineOptions()
        // -L -> symlink
        wslCommandLineOptions.addInitCommand(
            "(if [ -L " + ocaml + " ]; then" +
                    " if [ -L " + compiler + " ]; then" +
                    " if [ -L " + sources + " ]; then echo 0; else echo -3; fi;" +
                    " else echo -2; fi;" +
                    " else echo -1; fi;)"
        )
        try {
            cli = distribution.patchCommandLine(cli, null, wslCommandLineOptions)
            LOG.debug("The CLI was: " + cli.commandLineString)
            val process = cli.createProcess()
            process.waitFor()
            val exitCode = String(process.inputStream.readAllBytes()).replace("\n", "").toInt()
            LOG.debug("code:$exitCode")
            return if (exitCode == 0) InvalidHomeError.NONE else InvalidHomeError.GENERIC
        } catch (e: ExecutionException) {
            return null
        } catch (e: InterruptedException) {
            return null
        } catch (e: IOException) {
            return null
        } catch (e: NumberFormatException) {
            return null
        }
    }

    override fun getDuneVersion(sdkHomePath: String?): String? {
        val path = WslPath.parseWindowsUncPath((sdkHomePath)!!) ?: return null
        val distribution = path.distribution
        try {
            // create command
            val cli = GeneralCommandLine(path.linuxPath + "/bin/dune", "--version")
            // same code as for the base provider ><
            return String(
                distribution.patchCommandLine(cli, null, WSLCommandLineOptions())
                    .createProcess()
                    .inputStream
                    .readAllBytes()
            ).trim { it <= ' ' } // remove \n
        } catch (e: IOException) {
            LOG.warn("Get dune version error:" + e.message)
            return null
        } catch (e: ExecutionException) {
            LOG.warn("Get dune version error:" + e.message)
            return null
        }
    }

    companion object {
        // move to another class?
        fun expandUserHome(distro: WSLDistribution, folder: String): String {
            if (!folder.contains("~")) return folder
            val userHome = distro.userHome ?: return folder
            return folder.replace("~", userHome)
        }
    }
}