package com.dune.ide.annotator

import com.dune.ide.DuneBasePlatformTestCase
import com.dune.ide.colors.DuneColor
import org.junit.Test

class DuneHighlightingAnnotatorTest : DuneBasePlatformTestCase() {

    @Test
    fun testInstruction() {
        configureHighlight(
            "(<info>library</info>)",
            DuneColor.INSTRUCTION
        )
    }

    @Test
    fun testArgument() {
        configureHighlight(
            "(library\n" +
                " (<info>name</info> my_lib)",
            DuneColor.ARGUMENT,
            true
        )
    }

    @Test
    fun testParameter() {
        configureHighlight(
            "(library\n" +
                " (name <info>my_lib</info>)",
            DuneColor.PARAMETER,
            true
        )
    }
}