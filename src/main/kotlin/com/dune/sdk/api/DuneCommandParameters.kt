package com.dune.sdk.api

/**
 * @param duneFolderPath path to the folder with the Dune file
 * @param duneTargetName name of the target
 * @param workingDirectory the working directory
 * @param outputDirectory the output directory
 * @param env environment variables
 */
data class DuneCommandParameters(val command: DuneCommand, val duneTargetPath: String,
                                 val workingDirectory: String, val outputDirectory: String,
                                 val commandsArgs: String, val executableArgs: String,
                                 val env: MutableMap<String, String>)