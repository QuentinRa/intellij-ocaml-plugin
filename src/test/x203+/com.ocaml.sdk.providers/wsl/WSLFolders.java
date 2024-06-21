package com.ocaml.sdk.providers.wsl;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.wsl.WSLCommandLineOptions;
import com.intellij.execution.wsl.WSLDistribution;
import com.intellij.execution.wsl.WslDistributionManager;
import com.intellij.openapi.util.SystemInfo;
import com.ocaml.sdk.providers.BaseFolderProvider;
import com.ocaml.sdk.utils.SdkInfo;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

public class WSLFolders implements BaseFolderProvider {

    public @Nullable SdkInfo BIN_VALID_SDK;
    public @Nullable SdkInfo BIN_CREATE_SDK;
    public @Nullable String BIN_VALID;
    public @Nullable String OPAM_HOME;
    public @Nullable SdkInfo OPAM_VALID_SDK;
    public @Nullable SdkInfo OPAM_INVALID_DIST;
    public @Nullable String OPAM_INVALID;
    public @Nullable String OPAM_INVALID_BIN;

    public @Nullable String HOME_INVALID;
    public @Nullable String OCAML_BIN_INVALID;

    public WSLFolders() {
        // not available
        if (!isWSLCompatible()) return;

        // get distributions
        List<WSLDistribution> list = WslDistributionManager.getInstance().getInstalledDistributions();
        if (list.size() <= 0) return;

        // find
        for (WSLDistribution distribution : list) {
            // opam, if installed
            try {
                GeneralCommandLine cli = new GeneralCommandLine("opam", "switch", "show");
                cli = distribution.patchCommandLine(cli, null, new WSLCommandLineOptions());
                Process process = cli.createProcess();
                String version = new String(process.getInputStream().readAllBytes()).trim();
                if (version.isEmpty() || process.exitValue() != 0)
                    throw new ExecutionException("No version / switch.");

                /* path to the opam folder **/
                String opamFolder = distribution.getUserHome() + "/.opam/";
                OPAM_HOME = distribution.getWindowsPath(opamFolder);

                /* everything should be valid */
                OPAM_VALID_SDK = new SdkInfo(
                        OPAM_HOME+version, OPAM_HOME+version+"\\bin\\ocaml",
                        "", version, "\\lib\\ocaml"
                );

                OPAM_INVALID_DIST = new SdkInfo(
                        "\\\\wsl$\\Fedora\\home\\username\\.opam\\4.07.0",
                        "\\\\wsl$\\Fedora\\home\\username\\.opam\\4.07.0\\bin\\ocaml",
                        "",
                        ""
                );

                /* expected: properly formatted path, non-existing SDK version */
                OPAM_INVALID = OPAM_HOME+"\\0.00.0";
                OPAM_INVALID_BIN = OPAM_HOME+"\\0.00.0\\bin\\ocaml";
            } catch (ExecutionException | IOException ignore) {}

            // check native

            /* a valid file that is not ocaml **/
            BIN_VALID = distribution.getWindowsPath("/bin/find");

            try {
                GeneralCommandLine cli = new GeneralCommandLine("/bin/ocamlc", "-version");
                cli = distribution.patchCommandLine(cli, null, new WSLCommandLineOptions());
                Process process = cli.createProcess();
                String version = new String(process.getInputStream().readAllBytes()).trim();
                if (version.isEmpty() || process.exitValue() != 0)
                    throw new ExecutionException("No version.");

                cli = new GeneralCommandLine("true");
                WSLCommandLineOptions options = new WSLCommandLineOptions();
                options.addInitCommand("ocamlc -config | sed -nr 's/^standard_library: (.*)*/\\1/p'");
                cli = distribution.patchCommandLine(cli, null, options);
                process = cli.createProcess();
                String libFolder = new String(process.getInputStream().readAllBytes()).trim();
                if (libFolder.isEmpty() || process.exitValue() != 0)
                    throw new ExecutionException("No lib folder.");

                String root = distribution.getWindowsPath("/");
                String ocamlBin = distribution.getWindowsPath("/bin/ocaml");
                String ocamlCompilerBin = distribution.getWindowsPath("/bin/ocamlc");
                /* a valid binary candidate for an SDK **/
                BIN_VALID_SDK = new SdkInfo(root, ocamlBin,
                        "", version, libFolder.replace("/","\\")
                );
                BIN_CREATE_SDK = new SdkInfo(null,
                        ocamlBin,
                        ocamlCompilerBin,
                        version,
                        distribution.getWindowsPath(libFolder)
                );
            } catch (ExecutionException | IOException ignore) {}
        }

        HOME_INVALID = "\\\\wsl$\\Debian\\invalid";
        OCAML_BIN_INVALID = "\\\\wsl$\\Debian\\invalid\\ocaml";
    }

    private boolean isWSLCompatible () {
        return SystemInfo.isWin10OrNewer;
    }

    @Override public String getName() {
        return "WSL";
    }

    @Override public boolean isBinAvailable() {
        return BIN_CREATE_SDK != null || BIN_VALID_SDK != null;
    }

    @Override public boolean isOpamAvailable() {
        return OPAM_HOME != null || OPAM_VALID_SDK != null;
    }
}
