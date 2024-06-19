package com.ocaml.sdk.providers

import com.intellij.execution.configurations.GeneralCommandLine
import java.nio.file.Path


interface OCamlSdkProvider {
    /**
     * If a provider is made of multiples providers, you shall
     * return them using this method.
     *
     * @return null or a list of providers
     */
    val nestedProviders: List<OCamlSdkProvider>

    //
    // PATH
    //
    /**
     * @return A list of commands that are starting the
     * ocaml interactive toplevel (ex: "ocaml")
     */
    val oCamlTopLevelCommands: Set<String?>

    /**
     * @return A list of commands that are used to compile
     * ocaml files, if the command is in the path.
     * <br></br>
     * Values must be sorted by what's the most likely to be a
     * valid value.
     * <br></br>
     * Ex: "ocamlc"
     */
    val oCamlCompilerCommands: List<String?>

    /**
     * @return The folders in which sources may be stored.
     * Values must be sorted by what's the most likely to be a
     * valid value. The path is relative to the SDK root folder.
     */
    val oCamlSourcesFolders: List<String?>

    /**
     * @param ocamlBinary a possible path to an opam SDK
     * @return true if this ocamlBinary is an opam binary
     */
    fun isOpamBinary(ocamlBinary: String): Boolean?

    /**
     * Create an SDK using binaries. They should be linked, not copied.
     *
     * @param ocaml       location to the ocaml binary
     * @param compiler    location of the compiler
     * @param version     version of the compiler
     * @param sources     location of the sources folder
     * @param sdkFolder   the folder, that may not exist, in which the SDK should be stored.
     * @param sdkModifier the name of the SDK should be sdkFolder/version followed by sdkModifier
     * @return a path to the created SDK, or null
     */
    fun createSdkFromBinaries(
        ocaml: String?, compiler: String?, version: String?,
        sources: String?, sdkFolder: String?, sdkModifier: String?
    ): String?

    /**
     * The provider will try to return the associated compiler, if possible.
     *
     * @param ocamlBinary the path to the ocaml binary, may be invalid
     * @return null of the path to the ocamlc binary
     */
    fun getAssociatedBinaries(ocamlBinary: String): AssociatedBinaries?

    /**
     * @param sdkHome a valid sdk home
     * @return the path to the sources folders,
     * relatives to the sdk home. Usually, sources are only stored in "lib/".
     */
    fun getAssociatedSourcesFolders(sdkHome: String): Set<String?>

    /**
     * Usual installations folders
     *
     * @return a list of installation folder.
     * Paths may be relatives or absolutes.
     */
    val installationFolders: Set<String?>

    /**
     * @return tries to find existing OCaml SDKs on this computer.
     */
    fun suggestHomePaths(): Set<String?>

    /**
     * Check if an homePath is valid, the method should be fast
     * if possible, or avoid heavy operations. You should ensure that
     * an SDK is stored inside a folder with a version ([com.ocaml.sdk.utils.OCamlSdkVersionManager.parse])
     *
     * @param homePath an homePath
     * @return true if the homePath is valid for at least one provider
     * @see com.ocaml.sdk.utils.OCamlSdkVersionManager.parse
     */
    fun isHomePathValid(homePath: Path): Boolean?

    /**
     * @param homePath an invalid sdk home path
     * @return why this home path is invalid or null if no provider
     */
    fun isHomePathValidErrorMessage(homePath: Path): InvalidHomeError?

    //
    // Commands
    //
    /**
     * Return a command line that can be used to get the version
     * of the compiler
     *
     * @param ocamlcCompilerPath path to the ocaml compiler
     * @return "ocamlc -version"
     * or null if this provider cannot generate a command for this compiler
     */
    fun getCompilerVersionCLI(ocamlcCompilerPath: String?): GeneralCommandLine?

    /**
     * @param sdkHomePath path to the SDK home
     * @return "ocaml -no-version"
     */
    fun getREPLCommand(sdkHomePath: String?): GeneralCommandLine?

    /**
     * @param sdkHomePath            path to the SDK home
     * @param rootFolderForTempering Most of the time, the root is returned unchanged.
     * If the paths used are tempered, then this path too, should be tempered
     * (ex: WSL).
     * @param file                   the file we are compiling
     * @param outputDirectory        the output directory
     * @param executableName         the name of the generated executable
     * @return the command line with the root for tempering paths, or null
     */
    fun getCompileCommandWithCmt(
        sdkHomePath: String?,
        rootFolderForTempering: String?,
        file: String?,
        outputDirectory: String?,
        executableName: String?
    ): CompileWithCmtInfo?

    //
    // DUNE
    //
    /**
     * @param sdkHomePath path to the sdkHome
     * @return Version of dune
     */
    fun getDuneVersion(sdkHomePath: String?): String?
}

enum class InvalidHomeError {
    NONE,
    GENERIC,
    INVALID_HOME_PATH,
    NO_TOP_LEVEL,
    NO_COMPILER,
    NO_SOURCES,
}

class AssociatedBinaries(
    val ocamlBin: String, val compilerPath: String,
    val sourcesPath: String, val compilerVersion: String
) {
    override fun toString(): String {
        return "AssociatedBinaries{" +
                "ocamlBin='" + ocamlBin + '\'' +
                ", compilerPath='" + compilerPath + '\'' +
                ", sourcesPath='" + sourcesPath + '\'' +
                ", compilerVersion='" + compilerVersion + '\'' +
                '}'
    }
}

// -annot is deprecated since 4.13. An alternative is to use
// ./ocamlcmt -annot file.cmt. You can generate a .cmt with -bin-annot.
// As this still available in 4.14, I won't update :D.
class CompileWithCmtInfo(
    cli: GeneralCommandLine,
    baseRootFolderForTempering: String
) {
    private val cli: GeneralCommandLine

    /**
     * This root is consumed by [OCamlMessageAdaptor.temperPaths]
     * to provide an OS-independent path in the messages.
     */
    private val rootFolderForTempering: String

    init {
        var rootFolderForTempering = baseRootFolderForTempering
        this.cli = cli

        // must ends with a trailing slash
        if (!rootFolderForTempering.endsWith("/") && !rootFolderForTempering.endsWith("\\"))
            rootFolderForTempering += if (rootFolderForTempering.contains("/")) "/" else "\\"

        this.rootFolderForTempering = rootFolderForTempering
    }

    /**
     * @return the extension of the annotation file, without the dot (".").
     */
    val annotationFileExtension: String
        get() = "annot"

    override fun toString(): String {
        return "CompileWithCmtInfo{" +
                "cli=" + cli.commandLineString +
                ", rootFolderForTempering='" + rootFolderForTempering + '\'' +
                '}'
    }

    companion object {
        private const val OUTPUT_EXTENSION: String = ".out"

        /**
         * ocamlc
         * -c $file
         * -o $outputDirectory/$executableName+OUTPUT_EXTENSION
         * -I $outputDirectory
         * -w +A
         * -color=never
         * -annot
         */
        fun createAnnotatorCommand(
            compiler: String?, file: String, outputFile: String,
            outputDirectory: String?, workingDirectory: String?
        ): GeneralCommandLine {
            val cli = GeneralCommandLine(compiler)
            if (file.endsWith(".mli")) cli.addParameter("-c")
            // compile everything else
            // fix #71: adding extension
            cli.addParameters(
                file, "-o", outputFile + OUTPUT_EXTENSION,
                "-I", outputDirectory,
                "-w", "+A", "-color=never", "-annot"
            )
            // fix issue -I is adding, so the current directory
            // is included, and this may lead to problems later (ex: a file.cmi may be
            // used instead of the one in the output directory, because we found one in the
            // current directory)
            cli.setWorkDirectory(workingDirectory)
            // Merge stderr with stdout
            cli.isRedirectErrorStream = true
            return cli
        }
    }
}
