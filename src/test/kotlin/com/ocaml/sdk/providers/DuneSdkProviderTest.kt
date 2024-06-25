package com.ocaml.sdk.providers

import com.ocaml.OCamlBaseUtilityTest
import org.junit.Test

class DuneSdkProviderTest : OCamlBaseUtilityTest() {

    @Test
    fun testGetDunProjectLang() {
        assertEquals("2.9", OCamlSdkProviderDune.getDunProjectLang("2.9"))
        assertEquals("3.14", OCamlSdkProviderDune.getDunProjectLang("3.14.7"))
    }

}