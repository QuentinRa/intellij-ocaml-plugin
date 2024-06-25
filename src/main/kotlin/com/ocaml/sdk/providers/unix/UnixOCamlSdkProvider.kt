package com.ocaml.sdk.providers.unix


import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.SystemProperties
import com.ocaml.sdk.providers.*
import com.ocaml.sdk.utils.OCamlSdkScanner
import com.ocaml.sdk.utils.OCamlSdkVersionUtils
import com.ocaml.utils.OCamlPathUtils
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

/**
 * Provide the most common values for a method. Subclasses may override
 * these if they are not valid for them.
 */
open class UnixOCamlSdkProvider : OCamlSdkProvider {
    protected open fun canUseProviderForOCamlBinary(path: String): Boolean = path.endsWith("ocaml")
    protected open fun canUseProviderForHome(homePath: Path): Boolean = true
    protected open fun canUseProviderForHome(homePath: String): Boolean = true

    override val nestedProviders: List<OCamlSdkProvider> get() = ArrayList()
    override val oCamlTopLevelCommands: Set<String> get() = setOf("ocaml")
    override val oCamlCompilerCommands: List<String> get() = listOf("ocamlc")
    override val oCamlSourcesFolders: List<String>
        get() = listOf("lib/ocaml", "usr/lib/ocaml", "usr/local/lib/ocaml")
    override val installationFolders: Set<String>
        // commands
        get() {
            val installationFolders: MutableSet<String> = HashSet()
            // we know that we may find opam
            installationFolders.add(SystemProperties.getUserHome() + "/.opam")
            // we may have created simple SDKs
            //fixme: installationFolders.add(FileUtil.expandUserHome(SimpleSdkData.SDK_FOLDER))
            // is there any other places in which we may find ocaml SDKs?
            // ...
            return installationFolders
        }

    override fun getAssociatedSourcesFolders(sdkHome: String): Set<String> {
        return setOf(
            "lib",
            ".opam-switch/sources/" // we may have this one in opam SDKs
        )
    }

    override fun suggestHomePaths(): Set<String?> {
        val roots: MutableSet<Path> = HashSet()
        for (folder in installationFolders) {
            roots.add(Path.of(folder))
        }
        return OCamlSdkScanner.scanAll(roots, true)
    }

    override fun isHomePathValid(homePath: Path): Boolean? {
        if (!canUseProviderForHome(homePath)) return null
        return isHomePathValidErrorMessage(homePath) == InvalidHomeError.NONE
    }

    override fun isHomePathValidErrorMessage(homePath: Path): InvalidHomeError? {
        // version
        val ok: Boolean = OCamlSdkVersionUtils.isValid(homePath.toFile().name)
        if (!ok) {
            LOG.debug("Not a valid home name: $homePath")
            return InvalidHomeError.INVALID_HOME_PATH
        }
        // interactive toplevel
        var hasTopLevel = false
        for (exeName in oCamlTopLevelCommands) {
            hasTopLevel = Files.exists(homePath.resolve("bin/$exeName"))
            if (hasTopLevel) break
        }
        if (!hasTopLevel) {
            var link = handleSymlinkHomePath(homePath)
            if (link == null) {
                LOG.debug("Not top level found for $homePath")
                link = InvalidHomeError.NO_TOP_LEVEL
            }
            return link
        }
        // compiler
        var hasCompiler = false
        for (compilerName in oCamlCompilerCommands) {
            hasCompiler = Files.exists(homePath.resolve("bin/$compilerName"))
            if (hasCompiler) break
        }
        if (!hasCompiler) {
            LOG.debug("Not compiler found for $homePath")
            return InvalidHomeError.NO_COMPILER
        }
        // sources
        var hasSources = false
        var sourcesMissing = false
        var e = InvalidHomeError.NONE
        for (sourceFolder in oCamlSourcesFolders) {
            val path = homePath.resolve(sourceFolder)
            hasSources = Files.exists(path)
            if (!hasSources) continue
            // ensure that the directory is not empty
            val list = path.toFile().list()
            sourcesMissing = list == null || list.size == 0
            //            System.out.println("check "+path+" isn't empty?"+sourcesMissing);
            break
        }
        if (!hasSources) {
            LOG.debug("Not sources found for $homePath")
            e = InvalidHomeError.NO_SOURCES
        }
        if (sourcesMissing) {
            LOG.warn("Sources are missing for $homePath")
        }
        return e
    }

    protected open fun handleSymlinkHomePath(homePath: Path): InvalidHomeError? = null

//    private fun getCompilerVersionCLI(ocamlcCompilerPath: String?): GeneralCommandLine? =
//        GeneralCommandLine(ocamlcCompilerPath, "-version")

//    override fun getREPLCommand(sdkHomePath: String?): GeneralCommandLine? {
//        if (!canUseProviderForHome(sdkHomePath!!)) return null
//        return GeneralCommandLine("$sdkHomePath/bin/ocaml", "-no-version")
//    }

//    override fun getCompileCommandWithCmt(
//        sdkHomePath: String?,
//        rootFolderForTempering: String?,
//        file: String?,
//        outputDirectory: String?,
//        executableName: String?
//    ): CompileWithCmtInfo? {
//        if (!canUseProviderForHome(sdkHomePath!!)) return null
//        return CompileWithCmtInfo(
//            CompileWithCmtInfo.createAnnotatorCommand(
//                "$sdkHomePath/bin/ocamlc", file!!,
//                "$outputDirectory/$executableName",
//                outputDirectory, outputDirectory
//            ),  // nothing to change
//            rootFolderForTempering!!
//        )
//    }


    // compiler
//    override fun isOpamBinary(ocamlBinary: String): Boolean? = ocamlBinary.contains(".opam")
//    override fun createSdkFromBinaries(
//        ocaml: String, compiler: String, version: String,
//        sources: String, sdkFolder: String, sdkModifier: String
//    ): String? {
//        if (!canUseProviderForOCamlBinary(ocaml)) return null
//        val sdkFolderFile = File(FileUtil.expandUserHome(sdkFolder))
//        val sdkHome = FileUtil.findSequentNonexistentFile(sdkFolderFile, version + sdkModifier, "")
//        var ok = sdkHome.mkdirs()
//        ok = ok && File(sdkHome, "bin").mkdir()
//        if (!ok) LOG.debug("create 'bin' failed")
//        ok = ok && File(sdkHome, "lib").mkdir()
//        if (!ok) LOG.debug("create 'lib' failed")
//        ok = ok && OCamlPathUtils.createSymbolicLink(ocaml, sdkHome.path, LOG, "bin", "ocaml")
//        ok = ok && OCamlPathUtils.createSymbolicLink(compiler, sdkHome.path, LOG, "bin", "ocamlc")
//        ok = ok && OCamlPathUtils.createSymbolicLink(sources, sdkHome.path, LOG, "lib", "ocaml")
//        return if (ok) sdkHome.absolutePath else null
//    }

//    override fun getAssociatedBinaries(ocamlBinary: String): AssociatedBinaries? {
//        if (!canUseProviderForOCamlBinary(ocamlBinary)) return null
//        // check files exists
//        val ocamlBinPath = Path.of(ocamlBinary)
//        if (!Files.exists(ocamlBinPath)) {
//            LOG.debug("binary not found: $ocamlBinary")
//            return null
//        }
//        val binPath = ocamlBinPath.parent
//        if (!Files.exists(binPath)) { // useless?
//            LOG.debug("bin folder not found for $ocamlBinary")
//            return null
//        }
//        val root = binPath.parent
//        if (!Files.exists(root)) {  // useless?
//            LOG.debug("root folder not found for $ocamlBinary")
//            return null
//        }
//        var sourceFolder: String? = null
//
//        // find a valid source folder
//        for (source in oCamlSourcesFolders) {
//            val sourcePath = root.resolve(source)
//            if (!Files.exists(sourcePath)) continue
//            sourceFolder = sourcePath.toFile().absolutePath
//            break
//        }
//        if (sourceFolder == null) {
//            LOG.debug("Sources' folder not found for $ocamlBinary")
//            return null
//        }
//
//        // testing compilers
//        for (compilerName in oCamlCompilerCommands) {
//            LOG.trace("testing $compilerName")
//            val compilerPath = binPath.resolve(compilerName)
//            if (!Files.exists(binPath)) continue
//            val compiler = compilerPath.toFile().absolutePath
//
//            var version: String
//
//            // try to find the version using the compiler
//            val cli = getCompilerVersionCLI(compiler)
//            if (cli == null) {
//                LOG.debug("No cli for $compiler")
//                continue
//            }
//            LOG.debug("CLI for " + compiler + " is " + cli.commandLineString)
//            try {
//                val process = cli.createProcess()
//                val inputStream = process.inputStream
//                version = String(inputStream.readAllBytes()).trim { it <= ' ' }
//                LOG.info("Version of $compiler is '$version'.")
//                // if we got something better
//                val alt: String = OCamlSdkVersionUtils.parse(ocamlBinary)
//                if (!OCamlSdkVersionUtils.isUnknownVersion(alt)) version = alt
//            } catch (e: ExecutionException) {
//                LOG.debug("Command failed:" + e.message)
//                continue
//            } catch (e: IOException) {
//                LOG.debug("Command failed:" + e.message)
//                continue
//            }
//
//            return AssociatedBinaries(ocamlBinary, compiler, sourceFolder, version)
//        }
//
//        LOG.warn("No compiler found for $ocamlBinary")
//        return null
//    }

    // Dune
    override fun getDuneVersion(sdkHomePath: String?): String? {
        if (!canUseProviderForHome(sdkHomePath!!)) return null
        try {
            val s = String(
                GeneralCommandLine(OCamlSdkProviderDune.getDuneExecutable(sdkHomePath), "--version")
                    .createProcess()
                    .inputStream
                    .readAllBytes()
            ).trim { it <= ' ' } // remove \n
            return s.ifEmpty { null }
        } catch (e: IOException) {
            LOG.warn("Get dune version error:" + e.message)
            return null
        } catch (e: ExecutionException) {
            LOG.warn("Get dune version error:" + e.message)
            return null
        }
    }

    override fun getDuneExecCommand(sdkHomePath: String, duneFolderPath: String, duneTargetName: String, workingDirectory: String, outputDirectory: String, env: MutableMap<String, String>): GeneralCommandLine? {
        return null
    }

    companion object {
        @JvmStatic
        protected val LOG: Logger = Logger.getInstance(UnixOCamlSdkProvider::class.java)
    }
}
