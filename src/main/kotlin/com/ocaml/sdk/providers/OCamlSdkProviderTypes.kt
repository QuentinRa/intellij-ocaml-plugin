package com.ocaml.sdk.providers

import com.intellij.execution.configurations.GeneralCommandLine

enum class InvalidHomeError {
    NONE,
    GENERIC,
    INVALID_HOME_PATH,
    NO_TOP_LEVEL,
    NO_COMPILER,
    NO_SOURCES,
}

class AssociatedBinaries(
    val ocamlBin: String, val compilerPath: String,
    val sourcesPath: String, val compilerVersion: String
) {
    override fun toString(): String {
        return "AssociatedBinaries{" +
                "ocamlBin='" + ocamlBin + '\'' +
                ", compilerPath='" + compilerPath + '\'' +
                ", sourcesPath='" + sourcesPath + '\'' +
                ", compilerVersion='" + compilerVersion + '\'' +
                '}'
    }
}

// -annot is deprecated since 4.13. An alternative is to use
// ./ocamlcmt -annot file.cmt. You can generate a .cmt with -bin-annot.
// As this still available in 4.14, I won't update :D.
class CompileWithCmtInfo(
    cli: GeneralCommandLine,
    baseRootFolderForTempering: String
) {
    private val cli: GeneralCommandLine

    /**
     * This root is consumed by [OCamlMessageAdaptor.temperPaths]
     * to provide an OS-independent path in the messages.
     */
    private val rootFolderForTempering: String

    init {
        var rootFolderForTempering = baseRootFolderForTempering
        this.cli = cli

        // must ends with a trailing slash
        if (!rootFolderForTempering.endsWith("/") && !rootFolderForTempering.endsWith("\\"))
            rootFolderForTempering += if (rootFolderForTempering.contains("/")) "/" else "\\"

        this.rootFolderForTempering = rootFolderForTempering
    }

    /**
     * @return the extension of the annotation file, without the dot (".").
     */
    val annotationFileExtension: String
        get() = "annot"

    override fun toString(): String {
        return "CompileWithCmtInfo{" +
                "cli=" + cli.commandLineString +
                ", rootFolderForTempering='" + rootFolderForTempering + '\'' +
                '}'
    }

    companion object {
        private const val OUTPUT_EXTENSION: String = ".out"

        /**
         * ocamlc
         * -c $file
         * -o $outputDirectory/$executableName+OUTPUT_EXTENSION
         * -I $outputDirectory
         * -w +A
         * -color=never
         * -annot
         */
        fun createAnnotatorCommand(
            compiler: String?, file: String, outputFile: String,
            outputDirectory: String?, workingDirectory: String?
        ): GeneralCommandLine {
            val cli = GeneralCommandLine(compiler)
            if (file.endsWith(".mli")) cli.addParameter("-c")
            // compile everything else
            // fix #71: adding extension
            cli.addParameters(
                file, "-o", outputFile + OUTPUT_EXTENSION,
                "-I", outputDirectory,
                "-w", "+A", "-color=never", "-annot"
            )
            // fix issue -I is adding, so the current directory
            // is included, and this may lead to problems later (ex: a file.cmi may be
            // used instead of the one in the output directory, because we found one in the
            // current directory)
            cli.setWorkDirectory(workingDirectory)
            // Merge stderr with stdout
            cli.isRedirectErrorStream = true
            return cli
        }
    }
}