package com.ocaml.sdk.providers

import com.intellij.openapi.diagnostic.Logger
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.ocaml.sdk.utils.OCamlSdkHomeUtils
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.nio.file.Files
import java.nio.file.Path

@RunWith(JUnit4::class)
abstract class BaseSdkProviderTest : BasePlatformTestCase() {
    protected fun assertInstallationFolderWasSuggested(installationFolder: String) {
        val homePaths: MutableList<String> = OCamlSdkHomeUtils.suggestHomePaths().toMutableList()
        val p = Path.of(installationFolder)
        // cannot test anything
        if (!Files.exists(p)) {
            assertTrue(true)
            return
        }
        val files = p.toFile().listFiles()
        if (files == null) { // no files, done
            assertTrue(true)
            return
        }
        for (file in files) {
            val path = file.absolutePath
            if (!OCamlSdkHomeUtils.isValid(path)) continue
            assertTrue(homePaths.remove(path))
        }
    }
}

// Each SDK Provider that gives access to SDK folders
// Must implement this class
interface OCamlSdkProviderFolders {
    val name: String?
    val isOpamAvailable: Boolean
    val isBinAvailable: Boolean

    companion object {
        val LOG: Logger = Logger.getInstance("ocaml.tests")
    }
}

// Carry data about a SDK
data class OCamlTestSdkInfo @JvmOverloads constructor(
    val path: String?,
    val toplevel: String,
    val comp: String,
    val version: String,
    val sources: String? = null
) {
    override fun toString(): String {
        return "SdkInfo{" +
                "path='" + path + '\'' +
                ", version='" + version + '\'' +
                ", toplevel='" + toplevel + '\'' +
                ", sources='" + sources + '\'' +
                ", comp='" + comp + '\'' +
                '}'
    }
}
