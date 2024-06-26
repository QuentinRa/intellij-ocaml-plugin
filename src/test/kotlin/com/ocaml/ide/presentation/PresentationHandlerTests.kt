package com.ocaml.ide.presentation

import com.intellij.psi.PsiElement
import com.intellij.ui.icons.allIconsStable
import com.intellij.util.PlatformIcons
import com.ocaml.icons.OCamlIcons
import com.ocaml.ide.OCamlBasePlatformTestCase
import com.ocaml.language.psi.OCamlLetBinding
import com.ocaml.language.psi.OCamlValueBinding
import com.ocaml.language.psi.api.OCamlNamedElement
import org.junit.Test
import javax.swing.Icon

class PresentationHandlerTests : OCamlBasePlatformTestCase() {

    private var simpleVariableAssignation : OCamlLetBinding? = null
    private var simpleFunctionAssignation : OCamlLetBinding? = null
    private var simpleOperatorAssignation : OCamlLetBinding? = null

    private var simpleVariableDeclaration : OCamlValueBinding? = null
    private var simpleFunctionDeclaration : OCamlValueBinding? = null
    private var simpleOperatorDeclaration : OCamlValueBinding? = null

    override fun setUp() {
        super.setUp()
        val ocamlElements = configureOCaml<OCamlNamedElement>(
            """
               let x = 5 
               let f a b = () 
               let (+) a b = () 
            """
        )
        simpleVariableAssignation = ocamlElements[0] as? OCamlLetBinding
        simpleFunctionAssignation = ocamlElements[1] as? OCamlLetBinding
        simpleOperatorAssignation = ocamlElements[2] as? OCamlLetBinding

        val ocamlInterfaceElements = configureOCamlInterface<OCamlNamedElement>(
            """
               val x : int 
               val f : int -> int -> int 
               val ( + ) : int -> int -> int 
            """
        )
        println(ocamlInterfaceElements)
        simpleVariableDeclaration = ocamlInterfaceElements[0] as? OCamlValueBinding
        simpleFunctionDeclaration = ocamlInterfaceElements[1] as? OCamlValueBinding
        simpleOperatorDeclaration = ocamlInterfaceElements[2] as? OCamlValueBinding
    }

    override fun tearDown() {
        super.tearDown()
        simpleVariableAssignation = null
        simpleFunctionAssignation = null
        simpleOperatorAssignation = null

        simpleVariableDeclaration = null
        simpleFunctionDeclaration = null
        simpleOperatorDeclaration = null
    }

    private fun testPresentationForStructure(e: PsiElement, expectedName: String): List<Icon> {
        val presentation = getPresentationForStructure(e)
        val icon = presentation.getIcon(false)
        assertEquals(expectedName, presentation.presentableText)
        assertNull(presentation.locationString)
        assertNotNull(icon)
        val iconList = icon?.allIconsStable()
        assertNotNull(iconList)
        assertSize(2, iconList!!)
        return iconList
    }

    @Test
    fun testNamedPresentationForStructure() {
        // assignations
        val varAssignationIcons = testPresentationForStructure(simpleVariableAssignation!!, "x")
        assertEquals(OCamlIcons.Nodes.VARIABLE, varAssignationIcons[0])
        assertEquals(PlatformIcons.PUBLIC_ICON, varAssignationIcons[1])

        val funAssignationIcons = testPresentationForStructure(simpleFunctionAssignation!!, "f")
        assertEquals(OCamlIcons.Nodes.FUNCTION, funAssignationIcons[0])
        assertEquals(PlatformIcons.PUBLIC_ICON, funAssignationIcons[1])

        val opAssignationIcons = testPresentationForStructure(simpleOperatorAssignation!!, "( + )")
        assertEquals(OCamlIcons.Nodes.FUNCTION, opAssignationIcons[0])
        assertEquals(PlatformIcons.PUBLIC_ICON, opAssignationIcons[1])

        // declarations
        val varDeclarationIcons = testPresentationForStructure(simpleVariableDeclaration!!, "x")
        assertEquals(OCamlIcons.Nodes.VARIABLE, varDeclarationIcons[0])
        assertEquals(PlatformIcons.PUBLIC_ICON, varDeclarationIcons[1])

        val funDeclarationIcons = testPresentationForStructure(simpleFunctionDeclaration!!, "f")
        assertEquals(OCamlIcons.Nodes.FUNCTION, funDeclarationIcons[0])
        assertEquals(PlatformIcons.PUBLIC_ICON, funDeclarationIcons[1])

        val opDeclarationIcons = testPresentationForStructure(simpleOperatorDeclaration!!, "( + )")
        assertEquals(OCamlIcons.Nodes.FUNCTION, opDeclarationIcons[0])
        assertEquals(PlatformIcons.PUBLIC_ICON, opDeclarationIcons[1])
    }
}