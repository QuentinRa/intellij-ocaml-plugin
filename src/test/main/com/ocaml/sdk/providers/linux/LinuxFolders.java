package com.ocaml.sdk.providers.linux;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.util.SystemProperties;
import com.ocaml.sdk.providers.BaseFolderProvider;
import com.ocaml.sdk.utils.OCamlSdkVersionManager;
import com.ocaml.sdk.utils.SdkInfo;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LinuxFolders implements BaseFolderProvider {

    public @Nullable SdkInfo BIN_VALID_SDK;
    public @Nullable SdkInfo BIN_CREATE_SDK;
    public @Nullable String BIN_VALID;
    public @Nullable String BIN_INVALID;
    public @Nullable String OPAM_HOME;
    public @Nullable SdkInfo OPAM_VALID_SDK;
    public @Nullable String OPAM_INVALID;
    public @Nullable String OPAM_INVALID_BIN;
    public @Nullable String OCAML_BIN_INVALID;

    public LinuxFolders() {
        // not available
        if (!SystemInfo.isLinux) return;

        // fill binaries-related variables
        try {
            GeneralCommandLine cli = new GeneralCommandLine("/bin/ocaml", "-vnum");
            Process process = cli.createProcess();
            String version = new String(process.getInputStream().readAllBytes()).trim();
            process.waitFor();
            if (version.isEmpty() || process.exitValue() != 0)
                throw new ExecutionException("No version / switch.");

            // lib folder
            String libFolder = "/lib/ocaml";
            if (!Files.exists(Path.of(libFolder))) {
                libFolder = "/usr/lib/ocaml";
                // check again
                if (!Files.exists(Path.of(libFolder)))
                    throw new ExecutionException("Library not found");
            }

            /* a valid binary candidate for an SDK **/
            BIN_VALID_SDK = new SdkInfo(
                    "/",
                    "/bin/ocaml",
                    "",
                    version,
                    libFolder
            );

            BIN_CREATE_SDK = new SdkInfo(
                    null,
                    "/bin/ocaml",
                    "/bin/ocamlc",
                    version,
                    libFolder
            );
        } catch (IOException | InterruptedException | ExecutionException e) {
            LOG.warn(e.getMessage());
        }

        /* a valid file that is not ocaml **/
        BIN_VALID = "/bin/find";

        try {
            String opamHome = SystemProperties.getUserHome() +"/.opam/";
            Path opamHomePath = Path.of(opamHome);
            if (!Files.exists(opamHomePath)) {
                GeneralCommandLine c = new GeneralCommandLine("eval", "opam var -w | sed -nr 's/^root[[:space:]]*(.*)* #.*/\\1/p'");
                Process process = c.createProcess();
                opamHome = new String(process.getInputStream().readAllBytes()).trim();
                process.waitFor();
                if (opamHome.isEmpty() || process.exitValue() != 0)
                    throw new ExecutionException("Opam not installed");
            }
            /* path to the opam folder **/
            OPAM_HOME = opamHome;

            File[] files = opamHomePath.toFile().listFiles();
            if (files == null || files.length == 0) throw new ExecutionException("Opam folder empty");

            for (File file : files) {
                String version = file.getName();
                if (!OCamlSdkVersionManager.isValid(version)) continue;

                String path = opamHome+version+"/";

                /* everything should be valid **/
                OPAM_VALID_SDK = new SdkInfo(
                        path, opamHome+version+"/bin/ocaml", "", version,
                        "/lib/ocaml" // relative
                );

                /* expected: properly formatted path, non-existing SDK version **/
                OPAM_INVALID = opamHome+"0.0.0";
                OPAM_INVALID_BIN = opamHome+"0.0.0/bin/ocaml";
            }
        } catch (ExecutionException | IOException | InterruptedException e) {
            LOG.warn(e.getMessage());
        }

        BIN_INVALID = "/bin/invalid";
        OCAML_BIN_INVALID = "/invalid/ocaml";
    }

    @Override public String getName() {
        return "Linux";
    }

    @Override public boolean isBinAvailable() {
        return BIN_VALID_SDK != null || BIN_CREATE_SDK != null;
    }

    @Override public boolean isOpamAvailable() {
        return OPAM_VALID_SDK != null;
    }
}
