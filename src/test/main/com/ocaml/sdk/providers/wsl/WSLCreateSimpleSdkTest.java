package com.ocaml.sdk.providers.wsl;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.wsl.WSLCommandLineOptions;
import com.intellij.execution.wsl.WSLDistribution;
import com.intellij.execution.wsl.WslPath;
import com.intellij.openapi.options.ConfigurationException;
import com.ocaml.sdk.providers.simple.SimpleSdkData;
import com.ocaml.sdk.utils.SdkInfo;
import org.junit.Test;

import java.util.ArrayList;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class WSLCreateSimpleSdkTest extends WSLBaseTest {

    private void assertCreate(int i) {
        SdkInfo binCreateSdk = folders.BIN_CREATE_SDK;
        if (binCreateSdk == null) return; // cannot test

        ArrayList<String> homes = new ArrayList<>();
        try {
            try {
                for (; i > 0 ; i--) {
                    SimpleSdkData simpleSdkData = new SimpleSdkData(
                            binCreateSdk.toplevel,
                            binCreateSdk.comp,
                            binCreateSdk.version,
                            binCreateSdk.sources
                    );
                    homes.add(simpleSdkData.homePath);
                    assertWSLHomeValid(simpleSdkData.homePath);
                }
            } catch (ConfigurationException e) {
                fail(e.getMessage());
            }
        } finally {
            WslPath path = WslPath.parseWindowsUncPath(binCreateSdk.toplevel);
            assert path != null; // it should have been not null
            WSLDistribution distribution = path.getDistribution();
            WSLCommandLineOptions options = new WSLCommandLineOptions();
            for (String home:homes) {
                String wslPath = distribution.getWslPath(home);
                if (wslPath != null)
                    options.addInitCommand("rm -rf "+wslPath);
            }
            GeneralCommandLine cli = new GeneralCommandLine("true");
            try {
                cli = distribution.patchCommandLine(cli, null, options);
                Process process = cli.createProcess();
                process.waitFor();
            } catch (ExecutionException | InterruptedException ignore) {}
        }
    }

    @Test
    public void testCreate1Sdk() {
        assertCreate(1);
    }

    @Test
    public void testCreate2Sdks() {
        assertCreate(2);
    }

    @Test
    public void testCreate3Sdks() {
        assertCreate(3);
    }

}
