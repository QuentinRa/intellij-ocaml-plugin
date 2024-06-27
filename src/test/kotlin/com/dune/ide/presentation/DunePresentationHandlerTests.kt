package com.dune.ide.presentation

import com.dune.ide.DuneBasePlatformTestCase
import com.dune.language.psi.DuneList
import com.intellij.psi.PsiElement
import org.junit.Test

class DunePresentationHandlerTests : DuneBasePlatformTestCase() {
    private var duneLibrary : DuneList? = null
    private var duneLibraryName : DuneList? = null
    private var duneLibraryLibraries : DuneList? = null
    private var duneExecutable : DuneList? = null
    private var duneExecutableName : DuneList? = null
    private var duneExecutableLibraries : DuneList? = null

    override fun setUp() {
        super.setUp()
        val duneElements = configureDune<DuneList>(
            """
(library
 (name my_library)
 (libraries core))

(executable
 (name my_executable)
 (libraries core my_library))
            """

        )
        duneLibrary = duneElements[0]
        duneLibraryName = duneElements[1]
        duneLibraryLibraries = duneElements[2]
        duneExecutable = duneElements[3]
        duneExecutableName = duneElements[4]
        duneExecutableLibraries = duneElements[5]
    }

    override fun tearDown() {
        super.tearDown()
        duneLibrary = null
        duneLibraryName = null
        duneLibraryLibraries = null
        duneExecutable = null
        duneExecutableName = null
        duneExecutableLibraries = null
    }

    private fun testPresentationForStructure(e: PsiElement, expectedName: String) {
        val presentation = getPresentationForStructure(e)
        assertEquals(expectedName, presentation.presentableText)
        assertNull(presentation.locationString)
    }

    @Test
    fun testNamedPresentationForStructure() {
        testPresentationForStructure(duneLibrary!!, "library")
        testPresentationForStructure(duneLibraryName!!, "name")
        testPresentationForStructure(duneLibraryLibraries!!, "libraries")
        testPresentationForStructure(duneExecutable!!, "executable")
        testPresentationForStructure(duneExecutableName!!, "name")
        testPresentationForStructure(duneExecutableLibraries!!, "libraries")
    }
}