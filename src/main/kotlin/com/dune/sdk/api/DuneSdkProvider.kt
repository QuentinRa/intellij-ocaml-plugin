package com.dune.sdk.api

import com.intellij.execution.configurations.GeneralCommandLine

interface DuneSdkProvider {
    fun isDuneInstalled(sdkHomePath: String?): Boolean

    /**
     * @param sdkHomePath path to the sdkHome
     * @return Version of dune
     */
    fun getDuneVersion(sdkHomePath: String?): String?

    /**
     * "dune exec ${duneFolderPath}/test_hello_world.exe" (build+run)
     *
     * @param sdkHomePath path to the SDK home
     * @param args refer to DuneCommandParameters
     */
    fun getDuneExecCommand(sdkHomePath: String, args: DuneCommandParameters): GeneralCommandLine?
}