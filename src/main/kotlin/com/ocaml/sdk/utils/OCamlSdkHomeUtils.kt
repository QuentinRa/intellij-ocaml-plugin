package com.ocaml.sdk.utils

import com.ocaml.sdk.providers.InvalidHomeError
import com.ocaml.sdk.providers.OCamlSdkProvidersManager
import com.ocaml.sdk.providers.OCamlSdkProvidersManager.isHomePathValid
import com.ocaml.sdk.providers.OCamlSdkProvidersManager.isHomePathValidErrorMessage
import java.nio.file.Path
import java.util.ArrayList

object OCamlSdkHomeUtils {
    /**
     * Tries to find existing OCaml SDKs on this computer.
     * They are sorted by version, from newest to oldest.
     */
    fun suggestHomePaths(): List<String> {
        // wrap
        val homes = ArrayList<String>(OCamlSdkProvidersManager.suggestHomePaths())
        // reverse order (newer -> older)
        homes.sortWith { o1, o2 ->
            OCamlSdkVersionUtils.comparePaths(
                o2,
                o1
            )
        }
        return homes
    }

    fun defaultOCamlLocation(): String? {
        return null
    }

    fun isValid(homePath: String): Boolean {
        return isValid(Path.of(homePath))
    }

    fun isValid(homePath: Path): Boolean {
        return java.lang.Boolean.TRUE == isHomePathValid(homePath)
    }

    fun invalidHomeErrorMessage(homePath: Path): InvalidHomeError? {
        return isHomePathValidErrorMessage(homePath)
    }
}