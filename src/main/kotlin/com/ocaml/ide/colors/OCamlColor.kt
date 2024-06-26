package com.ocaml.ide.colors

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.OptionsBundle
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.ocaml.OCamlBundle
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors as Default

/**
 * All tokens that can have a color
 */
enum class OCamlColor(humanText: String, attr: TextAttributesKey? = null) {
    LOCAL_VARIABLE(OptionsBundle.message("options.language.defaults.local.variable"), Default.LOCAL_VARIABLE),
    GLOBAL_VARIABLE(OptionsBundle.message("options.language.defaults.global.variable"), Default.GLOBAL_VARIABLE),

    IDENTIFIER(OptionsBundle.message("options.language.defaults.identifier"), Default.IDENTIFIER),
    TYPE(OCamlBundle.message("settings.ocaml.color.type"), Default.CLASS_NAME),
    FUNCTION_DECLARATION(OptionsBundle.message("options.language.defaults.function.declaration"), Default.FUNCTION_DECLARATION),

    KEYWORD(OCamlBundle.message("settings.ocaml.color.keyword"), Default.KEYWORD),

    CHAR(OCamlBundle.message("settings.ocaml.color.char"), Default.STRING),
    NUMBER(OCamlBundle.message("settings.ocaml.color.number"), Default.NUMBER),
    STRING(OCamlBundle.message("settings.ocaml.color.string"), Default.STRING),

    COMMENT(OCamlBundle.message("settings.ocaml.color.comment"), Default.BLOCK_COMMENT),
    DOC_COMMENT(OCamlBundle.message("settings.ocaml.color.doc.comment"), Default.DOC_COMMENT),

    BRACES(OptionsBundle.message("options.language.defaults.braces"), Default.BRACES),
    BRACKETS(OptionsBundle.message("options.language.defaults.brackets"), Default.BRACKETS),
    OPERATORS(OCamlBundle.message("settings.ocaml.color.operation.sign"), Default.OPERATION_SIGN),
    SEMICOLON(OptionsBundle.message("options.language.defaults.semicolon"), Default.SEMICOLON),
    DOT(OptionsBundle.message("options.language.defaults.dot"), Default.DOT),
    COMMA(OptionsBundle.message("options.language.defaults.comma"), Default.COMMA),
    PARENTHESES(OptionsBundle.message("options.language.defaults.parentheses"), Default.PARENTHESES),

    ATTRIBUTE(OCamlBundle.message("settings.ocaml.color.attribute"), Default.METADATA),
    ;

    val textAttributesKey = TextAttributesKey.createTextAttributesKey("com.ocaml.$name", attr)
    val attributesDescriptor = AttributesDescriptor(humanText, textAttributesKey)
}