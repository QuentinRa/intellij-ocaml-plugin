package com.ocaml.sdk.providers.windows

import com.ocaml.sdk.providers.OCamlSdkProvider
import com.ocaml.sdk.providers.unix.UnixOCamlSdkProvider
import com.ocaml.sdk.utils.OCamlSdkScanner
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

/**
 * Windows is not natively supporting ocaml.
 * We are calling providers such as, WSL, Cygwin, Ocaml64, etc.
 */
open class WindowsOCamlSdkProvider : UnixOCamlSdkProvider() {
    private val myProviders: Array<OCamlSdkProvider> = WindowProviderUtil.createWindowsProviders()

    // There is no such thing on Windows
    override fun canUseProviderForOCamlBinary(path: String): Boolean = false
    override fun canUseProviderForHome(homePath: Path): Boolean = false
    override fun canUseProviderForHome(homePath: String): Boolean = false
    override val oCamlCompilerCommands: List<String> get() = listOf()
    override val oCamlTopLevelCommands: Set<String> get() = setOf()
    override val installationFolders: Set<String> get() = setOf()
    override val nestedProviders: List<OCamlSdkProvider>
        // but, we can install WSL, Cygwin, etc.
        get() {
            val nestedProviders: MutableList<OCamlSdkProvider> = mutableListOf()
            nestedProviders.addAll(myProviders)
            return nestedProviders
        }

    // fixme: handle suggestHomePaths() nicely, e.g. who is providing paths?
    // WSL is operating like Unix for now
    override fun suggestHomePaths(): Set<String> {
        val fsRoots = FileSystems.getDefault().rootDirectories
            ?: return emptySet()

        val installationDirectories = HashSet<Path>()
        val roots = HashSet<Path>()
        for (p in myProviders) {
            for (installationDirectory in p.installationFolders) {
                val installationDirectoryPath = Path.of(installationDirectory)
                val absolute =
                    installationDirectoryPath.isAbsolute || installationDirectory.startsWith("\\\\") // UNC path
                if (absolute) roots.add(installationDirectoryPath)
                else installationDirectories.add(installationDirectoryPath)
            }
        }

        LOG.debug("Roots found (1/2):$roots")
        LOG.debug("Installation directories to explore:$installationDirectories")

        for (root in fsRoots) {
            if (!Files.exists(root)) continue
            for (dir in installationDirectories) {
                roots.add(root.resolve(dir))
            }
        }

        // we may have created simple SDKs
        //fixme: roots.add(Path.of(FileUtil.expandUserHome(SimpleSdkData.SDK_FOLDER)))

        LOG.debug("Roots found (2/2):$roots")

        return OCamlSdkScanner.scanAll(roots, true)
    }
}