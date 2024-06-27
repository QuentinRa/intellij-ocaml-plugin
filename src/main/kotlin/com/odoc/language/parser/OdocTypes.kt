package com.odoc.language.parser

import com.intellij.psi.tree.IElementType
import com.odoc.OdocLanguage

interface OdocTypes {
    companion object {
        @JvmField
        val ATOM: IElementType = IElementType("ATOM", OdocLanguage)
        @JvmField
        val BOLD: IElementType = IElementType("BOLD", OdocLanguage)
        @JvmField
        val CODE: IElementType = IElementType("CODE", OdocLanguage)
        @JvmField
        val COLON: IElementType = IElementType("COLON", OdocLanguage)
        @JvmField
        val CROSS_REF: IElementType = IElementType("CROSS_REF", OdocLanguage)
        @JvmField
        val EMPHASIS: IElementType = IElementType("EMPHASIS", OdocLanguage)
        @JvmField
        val ITALIC: IElementType = IElementType("ITALIC", OdocLanguage)
        @JvmField
        val LINK_START: IElementType = IElementType("LINK_START", OdocLanguage)
        @JvmField
        val LIST_ITEM_START: IElementType = IElementType("LIST_ITEM_START", OdocLanguage)
        @JvmField
        val NEW_LINE: IElementType = IElementType("NEW_LINE", OdocLanguage)
        @JvmField
        val COMMENT_START: IElementType = IElementType("COMMENT_START", OdocLanguage)
        @JvmField
        val COMMENT_END: IElementType = IElementType("COMMENT_END", OdocLanguage)
        @JvmField
        val O_LIST: IElementType = IElementType("O_LIST", OdocLanguage)
        @JvmField
        val PRE: IElementType = IElementType("PRE", OdocLanguage)
        @JvmField
        val RBRACE: IElementType = IElementType("RBRACE", OdocLanguage)
        @JvmField
        val SECTION: IElementType = IElementType("SECTION", OdocLanguage)
        @JvmField
        val TAG: IElementType = IElementType("TAG", OdocLanguage)
        @JvmField
        val U_LIST: IElementType = IElementType("U_LIST", OdocLanguage)
    }
}
