package com.odoc.ide

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.ocaml.ide.colors.OCamlColor
import com.odoc.ide.colors.OdocColor
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
abstract class OdocBasePlatformTestCase : BasePlatformTestCase() {
    protected fun configureHighlight(fileName: String, code: String, color: OdocColor, ignoreExtra: Boolean = false) {
        myFixture.configureByText(fileName, code.replace(
            "<info>",
            "<info descr=\"null\" textAttributesKey=\"${color.textAttributesKey.externalName}\">"
        ))
        myFixture.checkHighlighting(false, true, false, ignoreExtra)
    }
}