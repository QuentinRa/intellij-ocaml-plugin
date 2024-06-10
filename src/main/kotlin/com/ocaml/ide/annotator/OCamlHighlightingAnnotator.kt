package com.ocaml.ide.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.ocaml.ide.colors.OCamlColor
import com.ocaml.language.psi.OCamlTypes
import com.ocaml.language.psi.api.OCamlLetDeclaration
import com.ocaml.language.psi.api.OCamlNameIdentifierOwner

class OCamlHighlightingAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (holder.isBatchMode) return
        if (element !is LeafPsiElement) return
        val elementType = element.elementType

        // OCamlNameIdentifierOwner
        // todo: only works with "let", not generic at all (and a tad hard-coded)
        if (elementType != OCamlTypes.LOWERCASE_IDENT_VALUE) return
        val ancestor = element.parent // LOWERCASE_IDENT
            ?.parent // VALUE_NAME
            ?.parent // OCamlNameIdentifierOwner
        if (ancestor !is OCamlNameIdentifierOwner) return
        if (ancestor !is OCamlLetDeclaration) return
        val color =
            if (ancestor.isFunction()) OCamlColor.FUNCTION_DECLARATION
            else if (ancestor.isGlobal()) OCamlColor.GLOBAL_VARIABLE
            else OCamlColor.LOCAL_VARIABLE
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .textAttributes(color.textAttributesKey)
            .create()
    }
}