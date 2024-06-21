package com.ocaml.sdk.providers.cygwin;

import com.ocaml.sdk.providers.BaseSdkProviderTest;
import com.ocaml.sdk.providers.simple.DetectionResult;
import com.ocaml.sdk.providers.simple.OCamlNativeDetector;
import com.ocaml.sdk.utils.OCamlSdkHomeManager;
import com.ocaml.sdk.utils.SdkInfo;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.file.Path;
import java.util.Arrays;

@RunWith(Parameterized.class)
public class CygwinBaseTest extends BaseSdkProviderTest {

    private final String folderName;
    protected CygwinFolders folders;

    @Contract(pure = true) @Parameterized.Parameters(name= "{0}")
    public static @NotNull Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] { { CygwinFolders.CYGWIN_FOLDER }, { CygwinFolders.OCAML64_FOLDER } });
    }

    public CygwinBaseTest(String folderName) {
        this.folderName = folderName;
    }

    @Override protected void setUp() throws Exception {
        super.setUp();
        folders = new CygwinFolders(folderName);
    }

    @Override protected void tearDown() throws Exception {
        super.tearDown();
        folders = null;
    }

    @Test
    public void test() {
        assertTrue(true);
    }

    protected void assertCygwinDetectionValid(@NotNull SdkInfo info) {
        assertCygwinDetectionValid(info.toplevel, info.comp, info.version);
    }

    protected void assertCygwinDetectionValid(String ocamlBinary, String ocamlcName, String expectedVersion) {
        Path bin = Path.of(ocamlBinary).getParent();
        String root = bin.getParent().toFile().getAbsolutePath();
        String ocamlc = bin.resolve(ocamlcName).toFile().getAbsolutePath();
        DetectionResult detectionResult = OCamlNativeDetector.detectNativeSdk(ocamlBinary);
        assertEquals(ocamlBinary, detectionResult.ocaml);
        assertEquals(ocamlc, detectionResult.ocamlCompiler);
        assertEquals(root + "\\lib\\ocaml", detectionResult.sources);
        assertEquals(expectedVersion, detectionResult.version);
        assertFalse(detectionResult.isError);
    }

    protected void assertCygwinDetectionInvalid(String ocamlBin) {
        try {
            DetectionResult detectionResult = OCamlNativeDetector.detectNativeSdk(ocamlBin);
            if (detectionResult.isError) throw new AssertionError("OK");
        } catch (AssertionError e) {
            assertTrue(true);
            return;
        }
        fail("Supposed unreachable code");
    }

    protected void assertCygwinHomeValid(String homePath) {
        assertTrue(OCamlSdkHomeManager.isValid(homePath));
    }

    protected void assertCygwinHomeInvalid(String homePath) {
        assertFalse(OCamlSdkHomeManager.isValid(homePath));
    }
}
