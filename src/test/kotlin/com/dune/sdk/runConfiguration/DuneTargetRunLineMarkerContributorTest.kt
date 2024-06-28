package com.dune.sdk.runConfiguration

import com.dune.ide.DuneBasePlatformTestCase
import com.intellij.psi.PsiElement
import org.junit.Test

class DuneTargetRunLineMarkerContributorTest : DuneBasePlatformTestCase() {

    private fun assertInfoNotNull(code: String, match: String? = null) {
        configureCode("dune", code)
        val element =
            if (match == null) myFixture.elementAtCaret
            else myFixture.findElementByText(match, PsiElement::class.java)
        assertNotNull(element) ; element!!
        assertSize(1, element.children)
        val leaf = element.children[0]
        val marker = DuneTargetRunLineMarkerContributor()
        assertNotNull(marker.getInfo(leaf))
    }

    @Test
    fun test_executable_name() {
        assertInfoNotNull("(executable (name hello_world<caret>))")
    }

    @Test
    fun test_executable_quoted_name() {
        assertInfoNotNull("(executable (name \"hello_world\"))", "\"hello_world\"")
    }

    @Test
    fun test_executables_names() {
        assertInfoNotNull("(executables (names hello_world<caret>))")
    }
}