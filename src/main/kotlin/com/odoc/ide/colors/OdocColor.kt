package com.odoc.ide.colors

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.odoc.OdocBundle
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors as Default

/**
 * All tokens that can have a color
 */
enum class OdocColor(humanText: String, attr: TextAttributesKey? = null) {
    // extracted from JavaHighlightingColors
    PARAMETER(OdocBundle.message("settings.odoc.documentation.attribute"), Default.DOC_COMMENT_TAG_VALUE),
    ;

    val textAttributesKey = TextAttributesKey.createTextAttributesKey("com.odoc.$name", attr)
    val attributesDescriptor = AttributesDescriptor(humanText, textAttributesKey)
}