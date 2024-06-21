package com.ocaml.sdk;

import com.ocaml.OCamlBaseTest;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class OCamlSdkTypeTest extends OCamlBaseTest {

    @Contract(pure = true) private static @NotNull String makeAPIURLOld(String version) {
        return "https://ocaml.org/releases/" + version + "/htmlman/libref/index.html";
    }

    @Contract(pure = true) private static @NotNull String makeAPIURLNew(String version) {
        return "https://ocaml.org/releases/" + version + "/api/index.html";
    }

    @Contract(pure = true) private static @NotNull String makeManualURLOld(String version) {
        return "https://ocaml.org/releases/" + version + "/htmlman/index.html";
    }

    @Contract(pure = true) private static @NotNull String makeManualURLNew(String version) {
        return "https://ocaml.org/releases/" + version + "/manual/index.html";
    }

    @Test
    public void testDocumentationURL() {
        assertEquals(makeManualURLOld("4.05"), OCamlSdkType.getManualURL("4.05"));
        assertEquals(makeManualURLOld("4.05"), OCamlSdkType.getManualURL("4.05.1"));
        assertEquals(makeManualURLOld("4.08"), OCamlSdkType.getManualURL("4.08.1"));
        assertEquals(makeManualURLOld("4.08"), OCamlSdkType.getManualURL("4.08.0"));
        assertEquals(makeManualURLOld("4.10"), OCamlSdkType.getManualURL("4.10.0"));
        assertEquals(makeManualURLOld("4.11"), OCamlSdkType.getManualURL("4.11.0"));
        assertEquals(makeManualURLOld("4.11"), OCamlSdkType.getManualURL("4.11.0+trunk"));
        assertEquals(makeManualURLOld("4.11"), OCamlSdkType.getManualURL("4.11.0~alpha1"));

        assertEquals(makeManualURLNew("4.12"), OCamlSdkType.getManualURL("0.0"));
        assertEquals(makeManualURLNew("4.12"), OCamlSdkType.getManualURL("4.12.0"));
        assertEquals(makeManualURLNew("4.13"), OCamlSdkType.getManualURL("4.13.1"));
        assertEquals(makeManualURLNew("4.13"), OCamlSdkType.getManualURL("4.13.1~alpha"));
    }

    @Test
    public void testAPIURL() {
        assertEquals(makeAPIURLOld("4.05"), OCamlSdkType.getApiURL("4.05"));
        assertEquals(makeAPIURLOld("4.05"), OCamlSdkType.getApiURL("4.05.1"));
        assertEquals(makeAPIURLOld("4.08"), OCamlSdkType.getApiURL("4.08.1"));
        assertEquals(makeAPIURLOld("4.08"), OCamlSdkType.getApiURL("4.08.0"));
        assertEquals(makeAPIURLOld("4.10"), OCamlSdkType.getApiURL("4.10.0"));
        assertEquals(makeAPIURLOld("4.11"), OCamlSdkType.getApiURL("4.11.0"));
        assertEquals(makeAPIURLOld("4.11"), OCamlSdkType.getApiURL("4.11.0+trunk"));
        assertEquals(makeAPIURLOld("4.11"), OCamlSdkType.getApiURL("4.11.0~alpha1"));

        assertEquals(makeAPIURLNew("4.12"), OCamlSdkType.getApiURL("0.0"));
        assertEquals(makeAPIURLNew("4.12"), OCamlSdkType.getApiURL("4.12.0"));
        assertEquals(makeAPIURLNew("4.13"), OCamlSdkType.getApiURL("4.13.1"));
        assertEquals(makeAPIURLNew("4.13"), OCamlSdkType.getApiURL("4.13.1~alpha"));
    }

    @Test
    public void testGetMajorAndMinor() {
        assertEquals("4.05", OCamlSdkType.getMajorAndMinorVersion("4.05"));
        assertEquals("4.05", OCamlSdkType.getMajorAndMinorVersion("4.05.1"));
        assertEquals("4.05", OCamlSdkType.getMajorAndMinorVersion("4.05.1+trunk"));
        assertEquals("4.05", OCamlSdkType.getMajorAndMinorVersion("4.05.0~alpha1"));
        assertNull(OCamlSdkType.getMajorAndMinorVersion("4.0")); // invalid
    }

}
