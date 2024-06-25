package com.ocaml.sdk.providers

import com.intellij.execution.configurations.GeneralCommandLine
import java.nio.file.Path

interface OCamlSdkProvider : OCamlCustomSdkProvider, OCamlSdkProviderUtils,
    OCamlSdkProviderAnnot, OCamlSdkProviderREPL, OCamlSdkProviderDune, OCamlSdkProviderOpam {
    /**
     * If a provider is made of multiples providers, you shall
     * return them using this method.
     *
     * @return null or a list of providers
     */
    val nestedProviders: List<OCamlSdkProvider>

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
     * @param sdkHome a valid sdk home
     * @return the path to the sources folders,
     * relatives to the sdk home. Usually, sources are only stored in "lib/".
     */
    fun getAssociatedSourcesFolders(sdkHome: String): Set<String>

    /**
     * Usual installations folders
     *
     * @return a list of installation folder.
     * Paths may be relatives or absolutes.
     */
    val installationFolders: Set<String>

    /**
     * @return tries to find existing OCaml SDKs on this computer.
     */
    fun suggestHomePaths(): Set<String?>

    /**
     * Check if an homePath is valid, the method should be fast
     * if possible, or avoid heavy operations. You should ensure that
     * an SDK is stored inside a folder with a version ([com.ocaml.sdk.utils.OCamlSdkVersionUtils.parse])
     *
     * @param homePath an homePath
     * @return true if the homePath is valid for at least one provider
     * @see com.ocaml.sdk.utils.OCamlSdkVersionUtils.parse
     */
    fun isHomePathValid(homePath: Path): Boolean?

    /**
     * @param homePath an invalid sdk home path
     * @return why this home path is invalid or null if no provider
     */
    fun isHomePathValidErrorMessage(homePath: Path): InvalidHomeError?
}

interface OCamlSdkProviderUtils {
//    /**
//     * Return a command line that can be used to get the version
//     * of the compiler
//     *
//     * @param ocamlcCompilerPath path to the ocaml compiler
//     * @return "ocamlc -version"
//     * or null if this provider cannot generate a command for this compiler
//     */
//    fun getCompilerVersionCLI(ocamlcCompilerPath: String?): GeneralCommandLine?

//    /**
//     * The provider will try to return the associated compiler, if possible.
//     *
//     * @param ocamlBinary the path to the ocaml binary, may be invalid
//     * @return null of the path to the ocamlc binary
//     */
//    fun getAssociatedBinaries(ocamlBinary: String): AssociatedBinaries?
}

interface OCamlCustomSdkProvider {
//    /**
//     * Create an SDK using binaries. They should be linked, not copied.
//     *
//     * @param ocaml       location to the ocaml binary
//     * @param compiler    location of the compiler
//     * @param version     version of the compiler
//     * @param sources     location of the sources folder
//     * @param sdkFolder   the folder, that may not exist, in which the SDK should be stored.
//     * @param sdkModifier the name of the SDK should be sdkFolder/version followed by sdkModifier
//     * @return a path to the created SDK, or null
//     */
//    fun createSdkFromBinaries(
//        ocaml: String, compiler: String, version: String,
//        sources: String, sdkFolder: String, sdkModifier: String
//    ): String?
}

interface OCamlSdkProviderAnnot {
//    /**
//     * @param sdkHomePath            path to the SDK home
//     * @param rootFolderForTempering Most of the time, the root is returned unchanged.
//     * If the paths used are tempered, then this path too, should be tempered
//     * (ex: WSL).
//     * @param file                   the file we are compiling
//     * @param outputDirectory        the output directory
//     * @param executableName         the name of the generated executable
//     * @return the command line with the root for tempering paths, or null
//     */
//    fun getCompileCommandWithCmt(
//        sdkHomePath: String?,
//        rootFolderForTempering: String?,
//        file: String?,
//        outputDirectory: String?,
//        executableName: String?
//    ): CompileWithCmtInfo?
}

interface OCamlSdkProviderREPL {
//    /**
//     * @param sdkHomePath path to the SDK home
//     * @return "ocaml -no-version"
//     */
//    fun getREPLCommand(sdkHomePath: String?): GeneralCommandLine?
}

interface OCamlSdkProviderDune {
    companion object {
        const val DUNE_BUILD_DIR = "DUNE_BUILD_DIR"

        /**
         * @return the path to the dune binary relative to the SDK home
         */
        fun getDuneExecutable(sdkHomePath: String?): String = "$sdkHomePath/bin/dune"

        /**
         * @return "./{relative dune folder}/{targetName}.exe"
         */
        fun computeTargetName(wslDuneFolder: String, wslWorkingDirectory: String, duneTargetName: String): String {
            return "./${wslDuneFolder.replace(wslWorkingDirectory, "").removePrefix("/")}/$duneTargetName.exe"
        }
    }

    /**
     * @param sdkHomePath path to the sdkHome
     * @return Version of dune
     */
    fun getDuneVersion(sdkHomePath: String?): String?

    /**
     * "dune exec ${duneFolderPath}/test_hello_world.exe" (build+run)
     *
     * @param sdkHomePath path to the SDK home
     * @param duneFolderPath path to the folder with the Dune file
     * @param duneTargetName name of the target
     * @param workingDirectory the working directory
     * @param outputDirectory the output directory
     * @param env environment variables
     */
    fun getDuneExecCommand(sdkHomePath: String, duneFolderPath: String, duneTargetName: String,
                           workingDirectory: String, outputDirectory: String, env: MutableMap<String, String>): GeneralCommandLine?
}

interface OCamlSdkProviderOpam {
//    /**
//     * @param ocamlBinary a possible path to an opam SDK
//     * @return true if this ocamlBinary is an opam binary
//     */
//    fun isOpamBinary(ocamlBinary: String): Boolean?
}