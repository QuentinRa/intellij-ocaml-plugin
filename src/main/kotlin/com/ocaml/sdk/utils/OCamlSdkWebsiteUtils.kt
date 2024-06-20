package com.ocaml.sdk.utils

object OCamlSdkWebsiteUtils {
    private fun getMajorAndMinorVersion(version: String): String? {
        var newVersion = version
        if (!OCamlSdkVersionUtils.isValid(newVersion)) return null
        // if we got two ".", then we trunc the patch number
        val last = newVersion.lastIndexOf('.')
        if (last != newVersion.indexOf('.')) newVersion = newVersion.substring(0, last)
        return newVersion
    }

    fun getApiURL(version: String): String {
        var newVersion = getMajorAndMinorVersion(version)
        if (newVersion == null) newVersion = "4.12"
        if (OCamlSdkVersionUtils.isNewerThan("4.12", newVersion))
            return "https://ocaml.org/releases/$newVersion/api/index.html"
        return "https://ocaml.org/releases/$newVersion/htmlman/libref/index.html"
    }

    fun getManualURL(version: String): String {
        var newVersion = getMajorAndMinorVersion(version)
        if (newVersion == null) newVersion = "4.12"
        if (OCamlSdkVersionUtils.isNewerThan("4.12", newVersion))
            return "https://ocaml.org/releases/$version/manual/index.html"
        return "https://ocaml.org/releases/$version/htmlman/index.html"
    }
}