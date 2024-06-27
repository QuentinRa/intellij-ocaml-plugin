package com.odoc.ide.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.ocaml.language.psi.OCamlTypes
import java.util.regex.Pattern

/**
 * Add some brown for "parameters" (values between [], regardeless of the content)
 * in the documentation.
 */
class OdocDocumentationAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is PsiComment) return
        if (element.elementType != OCamlTypes.DOC_COMMENT) return

        val matcher = PARAMETERS_MATCHER.matcher(element.getText())
        val range = element.getTextRange()
        while (matcher.find()) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(range.cutOut(TextRange(matcher.start(), matcher.end())))
                .textAttributes(DOC_COMMENT_TAG_VALUE).create()
        }
    }

    companion object {
        // extracted from JavaHighlightingColors
        val DOC_COMMENT_TAG_VALUE: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
            "DOC_COMMENT_TAG_VALUE",
            DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE
        )

        val PARAMETERS_MATCHER: Pattern = Pattern.compile("(\\[[^]]*])")
    }
}
