package com.ocaml.sdk.providers

import com.intellij.openapi.diagnostic.Logger
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
open class BaseSdkProviderTest : BasePlatformTestCase()

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
