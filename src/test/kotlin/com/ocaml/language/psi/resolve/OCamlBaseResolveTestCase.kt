package com.ocaml.language.psi.resolve

import com.intellij.psi.PsiElement
import com.ocaml.ide.OCamlBasePlatformTestCase
import com.ocaml.language.psi.api.OCamlNameIdentifierOwner

abstract class OCamlBaseResolveTestCase : OCamlBasePlatformTestCase() {

    protected inline fun <reified T: PsiElement> assertReferenceToVariableEquals(expr: String, expectedVariableName: String) {
        val valuePath = myFixture.findElementByText(expr, T::class.java)
        val reference = valuePath.reference?.resolve()
        assertNotNull(valuePath) ; valuePath!!
        assertNotNull(reference) ; reference!!
        assertSize(1, valuePath.references)
        assertEquals(reference, valuePath.references[0].resolve())
        reference as OCamlNameIdentifierOwner
        assertEquals(expectedVariableName, reference.name)
    }
}