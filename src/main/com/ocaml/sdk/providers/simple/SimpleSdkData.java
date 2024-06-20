package com.ocaml.sdk.providers.simple;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ConfigurationException;
import com.ocaml.OCamlBundle;
import com.ocaml.sdk.providers.OCamlSdkProvidersManager;
import com.ocaml.sdk.utils.OCamlSdkVersionManager;
import com.ocaml.utils.logs.OCamlLogger;
import org.jetbrains.annotations.NotNull;

// fixme: multiple lines commented out

/**
 * A simple SDK is an SDK having the same folder structure
 * than opam, while using native binaries (ex: /bin/ocaml), for those
 * that can't install opam.
 * <br>
 * Some folders are missing, and we are only creating shortcuts to the real
 * folders, we are not generating/downloading anything.
 */
public class SimpleSdkData {

    private static final Logger LOG = OCamlLogger.getSdkInstance("create");

    /**
     * create simple SDKs are stored in this folder.
     * We are asserting that the path will always use
     * a relative path starting from "~/", but the final location may change.
     **/
    public static final String SDK_FOLDER = "~/.jdks/ocaml";

    /**
     * the path to the created SDK
     **/
    public @NotNull final String homePath;

    /**
     * Create the simple SDK and store the homePath in {@link #homePath}.
     * If the version is valid, then are assuming that every other value is valid.
     *
     * @param ocamlBinary         location to the ocaml binary
     * @param ocamlCompilerBinary location of the compiler
     * @param version             version of the compiler
     * @param ocamlSourcesFolder  location of the sources folder
     * @throws ConfigurationException sdk.ocaml.version.empty, if the version
     *                                wasn't submitted, meaning that every other value is invalid
     * @throws ConfigurationException a
     */
    public SimpleSdkData(@NotNull String ocamlBinary,
                         @NotNull String ocamlCompilerBinary,
                         @NotNull String version,
                         @NotNull String ocamlSourcesFolder) throws ConfigurationException {
        if (!OCamlSdkVersionManager.isValid(version))
            throw new ConfigurationException(OCamlBundle.message("project.wizard.sdk.ocaml.version.empty"));

        LOG.info(
                "The binary is " + ocamlBinary + ", the compiler is " + ocamlCompilerBinary
                        + ", the sources' folder is " + ocamlSourcesFolder + ", and the version is " + version + "."
        );

        String homePath = OCamlSdkProvidersManager.INSTANCE.createSdkFromBinaries(
                ocamlBinary, ocamlCompilerBinary, version, ocamlSourcesFolder,
                // ex: SDK_FOLDER/4.05.0+local, SDK_FOLDER/4.05.0+local1, etc.
                SDK_FOLDER, "+local"
        );

        if (homePath == null)
            throw new ConfigurationException(OCamlBundle.message("project.wizard.sdk.create.failed", SDK_FOLDER));

        this.homePath = homePath;
    }
}
