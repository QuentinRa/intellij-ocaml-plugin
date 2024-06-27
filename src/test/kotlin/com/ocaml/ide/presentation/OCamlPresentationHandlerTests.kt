package com.ocaml.ide.presentation

import com.intellij.psi.PsiElement
import com.intellij.ui.icons.allIconsStable
import com.intellij.util.PlatformIcons
import com.ocaml.icons.OCamlIcons
import com.ocaml.ide.OCamlBasePlatformTestCase
import com.ocaml.language.psi.OCamlLetBinding
import com.ocaml.language.psi.OCamlTypedef
import com.ocaml.language.psi.OCamlValueBinding
import com.ocaml.language.psi.api.OCamlNamedElement
import org.junit.Test
import javax.swing.Icon

class OCamlPresentationHandlerTests : OCamlBasePlatformTestCase() {

    private var simpleVariableAssignation : OCamlLetBinding? = null
    private var simpleFunctionAssignation : OCamlLetBinding? = null
    private var simpleOperatorAssignation : OCamlLetBinding? = null

    private var abstractTypeDefinition : OCamlTypedef? = null
    private var simpleTypeDefinition : OCamlTypedef? = null

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
               type t
               type u = unit
            """
        )
        simpleVariableAssignation = ocamlElements[0] as? OCamlLetBinding
        simpleFunctionAssignation = ocamlElements[1] as? OCamlLetBinding
        simpleOperatorAssignation = ocamlElements[2] as? OCamlLetBinding
        abstractTypeDefinition = ocamlElements[3] as? OCamlTypedef
        simpleTypeDefinition = ocamlElements[4] as? OCamlTypedef

        val ocamlInterfaceElements = configureOCamlInterface<OCamlNamedElement>(
            """
               val x : int 
               val f : int -> int -> int 
               val ( + ) : int -> int -> int 
            """
        )
        simpleVariableDeclaration = ocamlInterfaceElements[0] as? OCamlValueBinding
        simpleFunctionDeclaration = ocamlInterfaceElements[1] as? OCamlValueBinding
        simpleOperatorDeclaration = ocamlInterfaceElements[2] as? OCamlValueBinding
    }

    override fun tearDown() {
        super.tearDown()
        simpleVariableAssignation = null
        simpleFunctionAssignation = null
        simpleOperatorAssignation = null
        abstractTypeDefinition = null
        simpleTypeDefinition = null

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
    fun test_named_presentation_for_structure() {
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

        // Types
        val abstractTypeIcons = testPresentationForStructure(abstractTypeDefinition!!, "t")
        assertEquals(OCamlIcons.Nodes.TYPE, abstractTypeIcons[0])
        assertEquals(PlatformIcons.PUBLIC_ICON, abstractTypeIcons[1])

        val simpleTypeIcons = testPresentationForStructure(simpleTypeDefinition!!, "u")
        assertEquals(OCamlIcons.Nodes.TYPE, simpleTypeIcons[0])
        assertEquals(PlatformIcons.PUBLIC_ICON, simpleTypeIcons[1])

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