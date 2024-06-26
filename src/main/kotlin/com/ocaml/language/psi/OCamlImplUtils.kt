package com.ocaml.language.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.ocaml.language.psi.api.OCamlNamedElement
import com.ocaml.language.psi.api.OCamlStubElementType
import com.ocaml.language.psi.api.isAnonymous
import com.ocaml.language.psi.stubs.OCamlLetBindingStub
import com.ocaml.language.psi.stubs.impl.*

// nothing for now
class OCamlImplUtils {
    companion object {
        @JvmStatic
        fun nextSiblingWithTokenType(root: PsiElement, elementType: IElementType): PsiElement? {
            var found: PsiElement? = null
            var sibling = root.nextSibling
            while (sibling != null) {
                if (sibling.node.elementType === elementType) {
                    found = sibling
                    sibling = null
                } else {
                    sibling = sibling.nextSibling
                }
            }
            return found
        }

        @JvmStatic
        fun PsiElement.toLeaf(): PsiElement? = when (val psi = this) {
            is OCamlValueName -> if (psi.operatorName != null) psi.operatorName!!.firstChild // OCamlInfixOp or OCamlPrefixSymbol
                ?.firstChild // OCamlInfixSymbol or <TOKEN>
                ?.firstChild // <TOKEN>
            else psi.lowercaseIdent!!.firstChild
            else -> this
        }
    }
}

fun createStubIfNotAnonymous(node: ASTNode): Boolean {
    return (node.psi as? OCamlNamedElement)?.isAnonymous() == false
}

fun factory(name: String): OCamlStubElementType<*, *> = when (name) {
    "LET_BINDING" -> OCamlLetBindingStub.Type
    "VALUE_DESCRIPTION" -> OCamlValBindingStub.Type
    "TYPE_DEFINITION" -> OCamlTypeDefinitionStub.Type
    else -> error("Unknown element $name")
}