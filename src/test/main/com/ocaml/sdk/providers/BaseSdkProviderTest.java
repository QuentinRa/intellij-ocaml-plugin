package com.ocaml.sdk.providers;

import com.ocaml.OCamlBaseTest;
import com.ocaml.sdk.utils.OCamlSdkHomeManager;
import com.ocaml.utils.adaptor.UntilIdeVersion;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class BaseSdkProviderTest extends OCamlBaseTest {

    @UntilIdeVersion(release = "203")
    @SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase") @Test @Ignore
    public void testFake() {}

    protected void assertInstallationFolderWasSuggested(@NotNull String installationFolder) {
        List<String> homePaths = OCamlSdkHomeManager.suggestHomePaths();
        Path p = Path.of(installationFolder);
        // cannot test anything
        if (!Files.exists(p)) {
            assertTrue(true);
            return;
        }
        File[] files = p.toFile().listFiles();
        if (files == null) { // no files, done
            assertTrue(true);
            return;
        }
        for (File file : files) {
            String path = file.getAbsolutePath();
            if (!OCamlSdkHomeManager.isValid(path)) continue;
            assertTrue(homePaths.remove(path));
        }
    }
}
