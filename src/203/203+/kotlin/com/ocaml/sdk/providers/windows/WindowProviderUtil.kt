package com.ocaml.sdk.providers.windows

import com.ocaml.sdk.providers.OCamlSdkProvider

object WindowProviderUtil {
    fun createWindowsProviders(): Array<OCamlSdkProvider> {
        return arrayOf(
            WSLSdkProvider()
        )
    }
}