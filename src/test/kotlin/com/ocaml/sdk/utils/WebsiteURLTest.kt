package com.ocaml.sdk.utils

import com.ocaml.OCamlBaseUtilityTest
import org.junit.Test

class WebsiteURLTest : OCamlBaseUtilityTest() {
    private fun makeAPIURLOld(version: String): String = "https://ocaml.org/releases/$version/htmlman/libref/index.html"
    private fun makeAPIURLNew(version: String): String = "https://ocaml.org/releases/$version/api/index.html"
    private fun makeManualURLOld(version: String): String = "https://ocaml.org/releases/$version/htmlman/index.html"
    private fun makeManualURLNew(version: String): String = "https://ocaml.org/releases/$version/manual/index.html"

    @Test
    fun testDocumentationURL() {
        assertEquals(makeManualURLOld("4.05"), OCamlSdkWebsiteUtils.getManualURL("4.05"))
        assertEquals(makeManualURLOld("4.05"), OCamlSdkWebsiteUtils.getManualURL("4.05.1"))
        assertEquals(makeManualURLOld("4.08"), OCamlSdkWebsiteUtils.getManualURL("4.08.1"))
        assertEquals(makeManualURLOld("4.08"), OCamlSdkWebsiteUtils.getManualURL("4.08.0"))
        assertEquals(makeManualURLOld("4.10"), OCamlSdkWebsiteUtils.getManualURL("4.10.0"))
        assertEquals(makeManualURLOld("4.11"), OCamlSdkWebsiteUtils.getManualURL("4.11.0"))
        assertEquals(makeManualURLOld("4.11"), OCamlSdkWebsiteUtils.getManualURL("4.11.0+trunk"))
        assertEquals(makeManualURLOld("4.11"), OCamlSdkWebsiteUtils.getManualURL("4.11.0~alpha1"))

        assertEquals(makeManualURLNew("4.12"), OCamlSdkWebsiteUtils.getManualURL("0.0"))
        assertEquals(makeManualURLNew("4.12"), OCamlSdkWebsiteUtils.getManualURL("4.12.0"))
        assertEquals(makeManualURLNew("4.13"), OCamlSdkWebsiteUtils.getManualURL("4.13.1"))
        assertEquals(makeManualURLNew("4.13"), OCamlSdkWebsiteUtils.getManualURL("4.13.1~alpha"))
    }

    @Test
    fun testAPIURL() {
        assertEquals(makeAPIURLOld("4.05"), OCamlSdkWebsiteUtils.getApiURL("4.05"))
        assertEquals(makeAPIURLOld("4.05"), OCamlSdkWebsiteUtils.getApiURL("4.05.1"))
        assertEquals(makeAPIURLOld("4.08"), OCamlSdkWebsiteUtils.getApiURL("4.08.1"))
        assertEquals(makeAPIURLOld("4.08"), OCamlSdkWebsiteUtils.getApiURL("4.08.0"))
        assertEquals(makeAPIURLOld("4.10"), OCamlSdkWebsiteUtils.getApiURL("4.10.0"))
        assertEquals(makeAPIURLOld("4.11"), OCamlSdkWebsiteUtils.getApiURL("4.11.0"))
        assertEquals(makeAPIURLOld("4.11"), OCamlSdkWebsiteUtils.getApiURL("4.11.0+trunk"))
        assertEquals(makeAPIURLOld("4.11"), OCamlSdkWebsiteUtils.getApiURL("4.11.0~alpha1"))

        assertEquals(makeAPIURLNew("4.12"), OCamlSdkWebsiteUtils.getApiURL("0.0"))
        assertEquals(makeAPIURLNew("4.12"), OCamlSdkWebsiteUtils.getApiURL("4.12.0"))
        assertEquals(makeAPIURLNew("4.13"), OCamlSdkWebsiteUtils.getApiURL("4.13.1"))
        assertEquals(makeAPIURLNew("4.13"), OCamlSdkWebsiteUtils.getApiURL("4.13.1~alpha"))
    }

    @Test
    fun testGetMajorAndMinor() {
        assertEquals("4.05", OCamlSdkWebsiteUtils.getMajorAndMinorVersion("4.05"))
        assertEquals("4.05", OCamlSdkWebsiteUtils.getMajorAndMinorVersion("4.05.1"))
        assertEquals("4.05", OCamlSdkWebsiteUtils.getMajorAndMinorVersion("4.05.1+trunk"))
        assertEquals("4.05", OCamlSdkWebsiteUtils.getMajorAndMinorVersion("4.05.0~alpha1"))
        assertNull(OCamlSdkWebsiteUtils.getMajorAndMinorVersion("4.0")) // invalid
    }
}