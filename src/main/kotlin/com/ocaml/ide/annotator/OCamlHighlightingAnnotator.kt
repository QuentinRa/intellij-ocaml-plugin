package com.ocaml.ide.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.elementType
import com.ocaml.ide.colors.OCamlColor
import com.ocaml.language.psi.OCamlTypes
import com.ocaml.language.psi.api.OCamlLetDeclaration
import com.ocaml.language.psi.api.OCamlNameIdentifierOwner

class OCamlHighlightingAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (holder.isBatchMode) return
        if (element !is LeafPsiElement) return
        val elementType = element.elementType

        // Works for: TYPE, VAL, LET
        if (elementType != OCamlTypes.LOWERCASE_IDENT_VALUE) return
        val ancestor = element.parent // LOWERCASE_IDENT | LOWERCASE_IDENT_NO_UNDERSCORE
            ?.parent // VALUE_NAME | VALUE_NAME_NO_UNDERSCORE | TYPECONSTRNAME
            ?.parent // OCamlNameIdentifierOwner (Typedef, ...)
        if (ancestor !is OCamlNameIdentifierOwner) return

        // Determine the color
        val color = when (ancestor) {
            // LET and VAL
            is OCamlLetDeclaration -> {
                if (ancestor.isFunction()) OCamlColor.FUNCTION_DECLARATION
                else if (ancestor.isGlobal()) OCamlColor.GLOBAL_VARIABLE
                else OCamlColor.LOCAL_VARIABLE
            }
            // Type
            else -> OCamlColor.IDENTIFIER
        }
        //println("--------")
        //println(element.textRange)
        //println(ancestor.text)
        //println(ancestor.elementType)
        //println(color)

        // Apply the color
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .textAttributes(color.textAttributesKey)
            .create()
    }
}