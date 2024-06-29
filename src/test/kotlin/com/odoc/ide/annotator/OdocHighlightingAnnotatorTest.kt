package com.odoc.ide.annotator

import com.odoc.ide.OdocBasePlatformTestCase
import com.odoc.ide.colors.OdocColor
import org.junit.Test

class OdocHighlightingAnnotatorTest : OdocBasePlatformTestCase() {

    @Test
    fun test_parameter_highlight() {
        configureHighlight("dummy.ml", "(** <info>[_]</info>: test **)\nlet _ = 5", OdocColor.PARAMETER)
    }

    @Test
    fun test_fix_regex_usage() {
        configureHighlight("dummy.ml", "(** <info>[aaaa [] yyy]</info>: test **)\nlet _ = 5", OdocColor.PARAMETER)
    }

    @Test
    fun test_fix_use_absolute_range() {
        configureHighlight("dummy.ml", "(*test*)\n\n(** <info>[aaaa [] yyy]</info>: test **)\nlet _ = 5", OdocColor.PARAMETER)
    }
}