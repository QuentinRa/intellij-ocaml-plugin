package com.dune.sdk.utils

import com.dune.DuneBaseUtilityTest
import org.junit.Test

class DuneSdkProviderTest : DuneBaseUtilityTest() {

    @Test
    fun testGetDunProjectLang() {
        assertEquals("2.9", DuneSdkUtils.getDunProjectLang("2.9"))
        assertEquals("3.14", DuneSdkUtils.getDunProjectLang("3.14.7"))
    }

}