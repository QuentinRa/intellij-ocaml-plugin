package com.dune.language

import com.dune.language.parser.DuneParserDefinition
import com.dune.language.psi.DuneList
import com.ocaml.language.OCamlBaseParsingTestCase
import org.junit.Test

abstract class DuneParsingTestCase : OCamlBaseParsingTestCase("", DuneParserDefinition())

class DuneBasicLanguageTest : DuneParsingTestCase() {
    private var duneLibrary : DuneList? = null
    private var duneLibraryName : DuneList? = null
    private var duneLibraryLibraries : DuneList? = null

    override fun setUp() {
        super.setUp()
        val duneElements = initWith<DuneList>(
            """
(library
 (name my_library)
 (libraries core))
            """

        )
        duneLibrary = duneElements[0]
        duneLibraryName = duneElements[1]
        duneLibraryLibraries = duneElements[2]
    }

    override fun tearDown() {
        super.tearDown()
        duneLibrary = null
        duneLibraryName = null
        duneLibraryLibraries = null
    }

    @Test
    fun test_expected_parsing_tree() {
        // Test Dune Library
        assertNotNull(duneLibrary)
        val duneLibrary : DuneList = duneLibrary!!
        assertEquals(duneLibrary.value?.text, "library")
        assertSize(2, duneLibrary.argumentList)
        assertEquals(duneLibraryName, duneLibrary.argumentList[0].list)
        assertEquals(duneLibraryLibraries, duneLibrary.argumentList[1].list)

        // Test Dune Library Name
        val duneLibraryName : DuneList = duneLibraryName!!
        assertEquals(duneLibraryName.value?.text, "name")
        assertSize(1, duneLibraryName.argumentList)
        assertEquals("my_library", duneLibraryName.argumentList[0].atom?.text)

        // Test Dune Library Name
        val duneLibraryLibraries : DuneList = duneLibraryLibraries!!
        assertEquals(duneLibraryLibraries.value?.text, "libraries")
        assertSize(1, duneLibraryLibraries.argumentList)
        assertEquals("core", duneLibraryLibraries.argumentList[0].atom?.text)
    }
}