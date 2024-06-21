package com.ocaml.sdk.providers.cygwin;

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
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class CygwinFolders implements BaseFolderProvider {

    public static final String CYGWIN_FOLDER = "cygwin64";
    private static final String CYGWIN_FOLDER_ALT = "cygwin";
    public static final String OCAML64_FOLDER = "OCaml64";

    private final String installationFolderName;
    public @Nullable SdkInfo BIN_VALID_SDK;
    public @Nullable SdkInfo BIN_CREATE_SDK;
    public @Nullable String BIN_VALID_EXE;
    public @Nullable String OPAM_HOME;
    public @Nullable SdkInfo OPAM_VALID_SDK;
    public @Nullable String OPAM_INVALID_BIN;

    public @Nullable String HOME_INVALID;
    public @Nullable String OCAML_BIN_INVALID;
    public @Nullable String OCAML_BIN_INVALID_NO_EXE;

    public CygwinFolders(String installationFolderName) {
        this.installationFolderName = installationFolderName;
        if(!SystemInfo.isWindows) return;
        // resolve the installation folder
        String cygwinRootFolder = null;
        Iterable<Path> fsRoots = FileSystems.getDefault().getRootDirectories();
        for (Path root : fsRoots) {
            Path cygwin64 = root.resolve(installationFolderName);
            if (!Files.exists(cygwin64)) {
                // support for "cygwin"
                if (CYGWIN_FOLDER.equals(installationFolderName)) {
                    cygwin64 = root.resolve(CYGWIN_FOLDER_ALT);
                    if (Files.exists(cygwin64)) {
                        cygwinRootFolder = cygwin64.toFile().getAbsolutePath();
                        break;
                    }
                }
                continue;
            }
            cygwinRootFolder = cygwin64.toFile().getAbsolutePath();
            break;
        }
        System.out.println("cygwin folder:"+cygwinRootFolder);
        if (cygwinRootFolder == null) {
            LOG.warn("No folder for '"+installationFolderName+"'");
            return;
        }

        // fill binaries-related variables
        try {
            final String binaryPath = cygwinRootFolder+"\\bin\\ocaml.exe";

            GeneralCommandLine cli = new GeneralCommandLine(binaryPath, "-vnum");
            Process process = cli.createProcess();
            String version = new String(process.getInputStream().readAllBytes()).trim();
            process.waitFor();
            if (version.isEmpty() || process.exitValue() != 0)
                throw new ExecutionException("No version / switch.");

            String ocamlCompilerName = "ocamlc.opt.exe";

            // we may find ocamlc
            String ocamlPath = cygwinRootFolder+"\\bin\\ocamlc.exe";
            if (Files.exists(Path.of(ocamlPath))) ocamlCompilerName = "ocamlc.exe";

            // lib folder
            String libFolder = "\\lib\\ocaml";
            String libFolderPath = cygwinRootFolder+libFolder;
            if (!Files.exists(Path.of(libFolderPath))) {
                libFolder = "\\usr\\lib\\ocaml";
                libFolderPath = cygwinRootFolder + libFolder;
            }
            if (!Files.exists(Path.of(libFolderPath))) throw new ExecutionException("Library not found");

            /* a valid binary candidate for an SDK **/
            BIN_VALID_SDK = new SdkInfo(
                    cygwinRootFolder,
                    cygwinRootFolder+"\\bin\\ocaml.exe",
                    ocamlCompilerName,
                    version
            );

            BIN_CREATE_SDK = new SdkInfo(
                    null,
                    cygwinRootFolder+"\\bin\\ocaml.exe",
                    cygwinRootFolder+"\\bin\\"+ocamlCompilerName,
                    version,
                    cygwinRootFolder+libFolder
            );
        } catch (IOException | ExecutionException | InterruptedException e) {
            LOG.warn(e.getMessage());
            System.out.println(e.getMessage());
        }

        /* a valid file that is not ocaml **/
        BIN_VALID_EXE = cygwinRootFolder+"\\bin\\find.exe";

        try {
            String opamHome = cygwinRootFolder+"\\home\\"+ SystemProperties.getUserName() +"\\.opam\\";
            Path opamHomePath = Path.of(opamHome);
            System.out.println("home:"+opamHome);
            if (!Files.exists(opamHomePath)) throw new ExecutionException("Opam not installed");
            /* path to the opam folder **/
            OPAM_HOME = opamHome;

            File[] files = opamHomePath.toFile().listFiles();
            if (files == null || files.length == 0) throw new ExecutionException("Opam folder empty");

            for (File file : files) {
                // CI: error with .x
                String version = file.getName();
                if (!OCamlSdkVersionManager.isValid(version)) continue;

                String path = opamHome+version+"\\";

                // we may find ocamlc
                String ocamlCompilerName = "ocamlc.opt.exe";
                String ocamlPath = path+"\\bin\\ocamlc.exe";
                if (Files.exists(Path.of(ocamlPath))) ocamlCompilerName = "ocamlc.exe";

                /* everything should be valid **/
                OPAM_VALID_SDK = new SdkInfo(
                        path, opamHome+version+"\\bin\\ocaml.exe",
                        ocamlCompilerName,
                        version
                );

                /* expected: properly formatted path, non-existing SDK version **/
                OPAM_INVALID_BIN = opamHome + "0.0.0\\bin\\ocaml.exe";
            }
        } catch (ExecutionException e) {
            LOG.warn(e.getMessage());
            System.out.println(e.getMessage());
        }

        HOME_INVALID = cygwinRootFolder+"\\invalid";
        OCAML_BIN_INVALID = cygwinRootFolder+"\\invalid\\bin\\ocaml.exe";
        OCAML_BIN_INVALID_NO_EXE = cygwinRootFolder+"\\invalid\\bin\\ocaml";
    }

    // not installed in the "right" folder on the OS used for CI testing
    // so, functions may use this method and "disable" tests
    public boolean isCI() {
        return System.getenv("CI") != null;
    }

    @Override public String getName() {
        return installationFolderName;
    }

    @Override public boolean isBinAvailable() {
        return BIN_VALID_SDK != null || BIN_CREATE_SDK != null;
    }

    @Override public boolean isOpamAvailable() {
        return OPAM_VALID_SDK != null;
    }
}
