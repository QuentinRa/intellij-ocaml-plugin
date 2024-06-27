package com.odoc.ide.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.ocaml.language.psi.OCamlTypes
import com.odoc.ide.colors.OdocColor
import com.odoc.language.lexer.OdocLexerAdapter
import com.odoc.language.parser.OdocTypes

/**
 * Add some brown for "parameters" (values between [], regardeless of the content)
 * in the documentation.
 */
class OdocDocumentationAnnotator : Annotator {
    private val lexer = OdocLexerAdapter()

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is PsiComment) return
        if (element.elementType != OCamlTypes.DOC_COMMENT) return
        lexer.consumeTokens(element.text) {
            if (it.tokenType !== OdocTypes.CODE) return@consumeTokens
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(it.start, it.stop))
                .textAttributes(OdocColor.PARAMETER.textAttributesKey).create()
        }
    }
}
