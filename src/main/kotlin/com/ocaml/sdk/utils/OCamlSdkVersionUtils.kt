package com.ocaml.sdk.utils

import com.intellij.openapi.util.io.FileUtil
import com.ocaml.OCamlBundle.message
import java.util.regex.Pattern

object OCamlSdkVersionUtils {
    private val VERSION_PATH_REGEXP: Pattern = Pattern.compile(".*/[a-z-.]*(\\d\\.\\d\\d(\\.\\d)?([+~][0-9a-z-]+)*)/.*")

    private val VERSION_ONLY_REGEXP: Pattern = Pattern.compile(".*/[a-z-.]*(\\d\\.\\d\\d(\\.\\d)?)([+~][0-9a-z-]+)*/.*")

    // This is the first regex above, the part between the two slashes
    private val VERSION_REGEXP: Pattern = Pattern.compile("[a-z-.]*(\\d\\.\\d\\d(\\.\\d)?([+~][0-9a-z-]+)*)")

    /** unknown version  */
    internal val UNKNOWN_VERSION: String = message("sdk.version.unknown")

    fun isUnknownVersion(version: String): Boolean {
        return UNKNOWN_VERSION == version
    }

    /**
     * Return the version of an SDK given its home.
     * The version is, by design, always in the path,
     * so we are only extracting it.
     *
     * @param sdkHome a path (expected to be absolute, but it should work
     * for most relative paths as long as there is at least one /),
     * using \\ or / as file separator.
     * @return either UNKNOWN_VERSION or the version
     * @see .isUnknownVersion
     * @see .UNKNOWN_VERSION
     */
    fun parse(sdkHome: String): String {
        return parse(sdkHome, VERSION_PATH_REGEXP)
    }

    private fun parse(sdkHome: String, regexp: Pattern): String {
        var sdkHome = sdkHome
        if (sdkHome.isBlank()) return UNKNOWN_VERSION
        // use Linux paths, as we are using these in the regex
        sdkHome = FileUtil.toSystemIndependentName(sdkHome)
        if (!sdkHome.endsWith("/")) sdkHome += "/"
        // try to find the first group
        val matcher = regexp.matcher(sdkHome)
        if (matcher.matches()) return matcher.group(1)
        // no match
        return UNKNOWN_VERSION
    }

    /**
     * Unlike parse, only returns the version, without any modifier (the +followed by some stuff)
     *
     * @param sdkHome a path (expected to be absolute, but it should work
     * for most relative paths as long as there is at least one /),
     * using \\ or / as file separator.
     * @return the version (ex: 4.12.0, even if the real version is 4.12.0+mingw64)
     */
    // not tested as both regex are the same,
    // the other one is tested. THe only difference is the group
    // that will be recuperated
    fun parseWithoutModifier(sdkHome: String): String {
        return parse(sdkHome, VERSION_ONLY_REGEXP)
    }

    /**
     * @param base return true if version is newer than the base version
     * @param version the version tested with the base
     * @return true if version is newer (or equals) than the base
     */
    fun isNewerThan(base: String, version: String): Boolean {
        return compareVersions(version, base) >= 0
    }

    /**
     * Return true if a version is a valid SDK version
     */
    fun isValid(version: String): Boolean {
        return VERSION_REGEXP.matcher(version).matches()
    }

    /**
     * Compare two paths and returns
     *
     *  * **0**: they have the same version
     *  * **-1**: the second one has a newer version
     *  * **1**: the first one has a newer version
     *
     */
    fun comparePaths(p1: String, p2: String): Int {
        val v1 = parseWithoutModifier(p1)
        val v2 = parseWithoutModifier(p2)
        return compareVersions(v1, v2)
    }

    private fun compareVersions(v1: String, v2: String): Int {
        // missing one '.'
        var v1 = v1
        var v2 = v2
        val i1 = v1.lastIndexOf('.')
        val i2 = v2.lastIndexOf('.')
        if (i2 > i1) v1 += ".0"
        if (i1 > i2) v2 += ".0"

        // clamp between -1 and 1 (integers)
        val i = v1.compareTo(v2)
        return if (i == 0) 0 else if (i >= 1) 1 else -1
    }
}
