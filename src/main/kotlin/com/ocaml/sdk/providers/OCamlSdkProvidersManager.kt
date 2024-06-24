package com.ocaml.sdk.providers

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.util.SystemInfo
import com.ocaml.sdk.providers.unix.UnixOCamlSdkProvider
import com.ocaml.sdk.providers.windows.WindowsOCamlSdkProvider
import java.nio.file.Path

/**
 * The only provider that should be used outside.
 * It's loading providers if they should be available, and calling
 * every one of them each time the user request something.
 */
object OCamlSdkProvidersManager : OCamlSdkProvider {
    private val myProviders = ArrayList<OCamlSdkProvider>()

    init {
        val provider =
            if (SystemInfo.isWindows)
                WindowsOCamlSdkProvider()
            else
                UnixOCamlSdkProvider()
        myProviders.add(provider)
        myProviders.addAll(provider.nestedProviders)
    }

    override val nestedProviders: List<OCamlSdkProvider> get() = myProviders
    override val oCamlTopLevelCommands: Set<String?> get() = callProvidersValuesSet { it.oCamlTopLevelCommands }
    override val oCamlCompilerCommands: List<String?> get() = callProvidersValuesList { it.oCamlCompilerCommands }
    override val oCamlSourcesFolders: List<String?> get() = callProvidersValuesList { it.oCamlSourcesFolders }
    override val installationFolders: Set<String> get() = callProvidersValuesSet(OCamlSdkProvider::installationFolders)

//    override fun isOpamBinary(ocamlBinary: String): Boolean? =
//        callProvidersValue { provider -> provider.isOpamBinary(ocamlBinary) }

//    override fun createSdkFromBinaries(ocaml: String, compiler: String, version: String,
//                                       sources: String, sdkFolder: String, sdkModifier: String): String? =
//        callProvidersValue { provider ->
//            provider.createSdkFromBinaries(ocaml, compiler, version, sources, sdkFolder, sdkModifier)
//        }
//
//    override fun getAssociatedBinaries(ocamlBinary: String): AssociatedBinaries? =
//        callProvidersValue { provider -> provider.getAssociatedBinaries(ocamlBinary) }

//    override fun getREPLCommand(sdkHomePath: String?): GeneralCommandLine? =
//        callProvidersValue { provider -> provider.getREPLCommand(sdkHomePath) }

//    override fun getCompileCommandWithCmt(
//        sdkHomePath: String?,
//        rootFolderForTempering: String?,
//        file: String?,
//        outputDirectory: String?,
//        executableName: String?
//    ): CompileWithCmtInfo? = callProvidersValue { provider ->
//        provider.getCompileCommandWithCmt(sdkHomePath, rootFolderForTempering, file, outputDirectory, executableName)
//    }

    // --------------- OCamlSdkProvider
    override fun suggestHomePaths(): Set<String?> = callProvidersValue { obj ->
        return@callProvidersValue obj.suggestHomePaths().ifEmpty { null }
    } ?: emptySet()

    override fun isHomePathValid(homePath: Path): Boolean? =
        callProvidersValue { provider -> provider.isHomePathValid(homePath) }

    override fun isHomePathValidErrorMessage(homePath: Path): InvalidHomeError? =
        callProvidersValue { provider -> provider.isHomePathValidErrorMessage(homePath) }

    override fun getAssociatedSourcesFolders(sdkHome: String): Set<String> =
        callProvidersValue { provider -> provider.getAssociatedSourcesFolders(sdkHome) } ?: emptySet()

    // OCamlSdkProviderDune
    override fun getDuneExecCommand(sdkHomePath: String, duneFolderPath: String, duneTargetName: String, workingDirectory: String, outputDirectory: String, env: MutableMap<String, String>) =
        callProvidersValue { provider -> provider.getDuneExecCommand(sdkHomePath, duneFolderPath, duneTargetName, workingDirectory, outputDirectory, env) }

    override fun getDuneVersion(sdkHomePath: String?): String  {
        var result : String? = null
        if (sdkHomePath != null) {
            result = callProvidersValue { provider ->
                val v = provider.getDuneVersion(sdkHomePath)
                return@callProvidersValue if (v.isNullOrBlank()) null else v
            }
        }
        return result ?: "2.9" // default is 2.9
    }

    // call providers
    private fun <R> callProvidersValuesSet(computeValues: (OCamlSdkProvider) -> Set<R>): Set<R> {
        val values = HashSet<R>()
        for (p in myProviders) values.addAll(computeValues(p))
        return values
    }
    private fun <R> callProvidersValuesList(computeValues:(OCamlSdkProvider) -> List<R>): List<R> {
        val values: MutableList<R> = ArrayList()
        for (p in myProviders) values.addAll(computeValues(p))
        return values
    }
    private fun <R> callProvidersValue(computeValues: (OCamlSdkProvider) -> R): R? {
        for (p in myProviders) {
            val call = computeValues(p)
            if (call != null) return call
        }
        return null
    }
}
