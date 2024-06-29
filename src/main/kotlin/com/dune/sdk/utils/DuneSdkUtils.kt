package com.dune.sdk.utils

object DuneSdkUtils {
    const val DUNE_BUILD_DIR = "DUNE_BUILD_DIR"

    /**
     * @return the path to the dune binary relative to the SDK home
     */
    fun getDuneExecutable(sdkHomePath: String): String = "$sdkHomePath/bin/dune"

    /**
     * @return convert "X.Y.Z" to "X.Y"
     */
    fun getDunProjectLang(duneVersion: String?) : String {
        if (duneVersion == null) return "2.9" // default
        val last = duneVersion.lastIndexOf('.')
        if (last != duneVersion.indexOf('.')) return duneVersion.substring(0, last)
        return duneVersion
    }

    /**
     * @return "./{relative dune folder}/{targetName}.exe"
     */
    fun computeTargetName(wslDuneFolder: String, wslWorkingDirectory: String, duneTargetName: String): String {
        val extension = ".exe"
        return "./${wslDuneFolder.replace(wslWorkingDirectory, "").removePrefix("/")}/$duneTargetName$extension"
    }
}