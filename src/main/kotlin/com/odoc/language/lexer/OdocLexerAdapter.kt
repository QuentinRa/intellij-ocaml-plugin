package com.odoc.language.lexer

import com.intellij.psi.tree.IElementType
import com.odoc.language.parser.OdocTypes
import java.io.IOException

class OdocLexerAdapter : _OdocLexer() {
    data class ParsedTokenInfo(val tokenType: IElementType, val start: Int, val stop: Int)

    fun consumeTokens(text: String, callback: (ParsedTokenInfo) -> Unit) {
        reset(text, 0, text.length, YYINITIAL)

        try {
            var tokenType = advance()
            while (tokenType != null) {
                when (tokenType) {
                    OdocTypes.COMMENT_START, OdocTypes.COMMENT_END -> {}
                    else -> {
                        callback(ParsedTokenInfo(tokenType, tokenStart, tokenEnd))
                    }
                }

                tokenType = advance()
            }
        } catch (_: IOException) {}
    }
}