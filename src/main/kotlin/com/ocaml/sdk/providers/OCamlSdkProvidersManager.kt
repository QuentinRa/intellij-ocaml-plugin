package com.ocaml.sdk.providers

import com.intellij.execution.configurations.GeneralCommandLine
import java.nio.file.Path

/**
 * The only provider that should be used outside.
 * It's loading providers if they should be available, and calling
 * every one of them each time the user request something.
 */
object OCamlSdkProvidersManager : OCamlSdkProvider {
    private val myProviders = ArrayList<OCamlSdkProvider>()

//    init {
//        if (SystemInfo.isWindows) {
//            addProvider(WindowsOCamlSdkProvider())
//        } else {
//            addProvider(BaseOCamlSdkProvider())
//        }
//    }

    // providers
    private fun addProvider(provider: OCamlSdkProvider) {
        myProviders.add(provider)
        val nestedProviders = provider.nestedProviders
        myProviders.addAll(nestedProviders)
    }

    override val nestedProviders: List<OCamlSdkProvider>
        get() = myProviders

    override val oCamlTopLevelCommands: Set<String?> get() = callProvidersValuesSet { it.oCamlTopLevelCommands }
    override val oCamlCompilerCommands: List<String?> get() = callProvidersValuesList { it.oCamlCompilerCommands }
    override val oCamlSourcesFolders: List<String?> get() = callProvidersValuesList { it.oCamlSourcesFolders }

    override fun isOpamBinary(ocamlBinary: String): Boolean? =
        callProvidersValue { provider -> provider.isOpamBinary(ocamlBinary) }

    override fun createSdkFromBinaries(ocaml: String?, compiler: String?, version: String?,
                                       sources: String?, sdkFolder: String?, sdkModifier: String?): String? =
        callProvidersValue { provider ->
            provider.createSdkFromBinaries(ocaml, compiler, version, sources, sdkFolder, sdkModifier)
        }

    override fun getAssociatedBinaries(ocamlBinary: String): AssociatedBinaries? =
        callProvidersValue { provider -> provider.getAssociatedBinaries(ocamlBinary) }

    override fun getAssociatedSourcesFolders(sdkHome: String): Set<String?> =
        callProvidersValue { provider -> provider.getAssociatedSourcesFolders(sdkHome) } ?: emptySet()

    override fun getCompilerVersionCLI(ocamlcCompilerPath: String?): GeneralCommandLine? =
        callProvidersValue { provider -> provider.getCompilerVersionCLI(ocamlcCompilerPath) }

    override fun getREPLCommand(sdkHomePath: String?): GeneralCommandLine? =
        callProvidersValue { provider -> provider.getREPLCommand(sdkHomePath) }

    override fun getCompileCommandWithCmt(
        sdkHomePath: String?,
        rootFolderForTempering: String?,
        file: String?,
        outputDirectory: String?,
        executableName: String?
    ): CompileWithCmtInfo? = callProvidersValue { provider ->
        provider.getCompileCommandWithCmt(sdkHomePath, rootFolderForTempering, file, outputDirectory, executableName)
    }

    override val installationFolders: Set<String?>
        get() = callProvidersValuesSet(OCamlSdkProvider::installationFolders)

    override fun suggestHomePaths(): Set<String?> = callProvidersValue { obj -> obj.suggestHomePaths() } ?: emptySet()

    override fun isHomePathValid(homePath: Path): Boolean? =
        callProvidersValue { provider -> provider.isHomePathValid(homePath) }

    override fun isHomePathValidErrorMessage(homePath: Path): InvalidHomeError? =
        callProvidersValue { provider -> provider.isHomePathValidErrorMessage(homePath) }

    override fun getDuneVersion(sdkHomePath: String?): String  {
        var result : String? = null
        if (sdkHomePath != null) {
            result = callProvidersValue { provider ->
                provider.getDuneVersion(sdkHomePath)
            }
        }
        return result ?: "2.9" // default is 2.9
    }

    // call providers
    private fun <R> callProvidersValuesSet(computeValues: (OCamlSdkProvider) -> Set<R?>): Set<R?> {
        val values = HashSet<R?>()
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
