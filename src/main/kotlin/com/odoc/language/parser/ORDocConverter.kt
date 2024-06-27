package com.odoc.language.parser

import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.lexer.FlexLexer
import com.intellij.openapi.util.text.HtmlBuilder
import com.intellij.openapi.util.text.HtmlChunk
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import java.io.IOException

/**
 * @see DocumentationManagerUtil
 *
 * @see DocumentationMarkup
 *
 * @see DocumentationManagerProtocol
 */
abstract class ORDocConverter {
    abstract fun convert(text: String): HtmlBuilder

    @Throws(IOException::class)
    protected fun skipWhiteSpace(lexer: FlexLexer): IElementType? {
        var elementType = lexer.advance()
        while (elementType != null && elementType === TokenType.WHITE_SPACE) {
            elementType = lexer.advance()
        }
        return elementType
    }

    protected fun extractRaw(startOffset: Int, endOffset: Int, text: CharSequence): String {
        return (text as String).substring(startOffset, text.length - endOffset)
    }

    protected fun extract(startOffset: Int, endOffset: Int, text: CharSequence): String {
        return (text as String).substring(startOffset, text.length - endOffset).trim { it <= ' ' }
    }

    open class ORDocHtmlBuilder {
        val myBuilder: HtmlBuilder = HtmlBuilder()
        val myChildren: MutableList<HtmlChunk> = ArrayList()

        fun appendChildren(wrap: Boolean) {
            if (!myChildren.isEmpty()) {
                trimEndChildren(myChildren)
                if (!myChildren.isEmpty()) {
                    if (wrap) {
                        myBuilder.append(HtmlChunk.p().children(myChildren))
                    } else {
                        for (chunk in myChildren) {
                            myBuilder.append(chunk)
                        }
                    }
                    myChildren.clear()
                }
            }
        }

        fun addSpace() {
            if (!myChildren.isEmpty()) {
                val lastIndex = myChildren.size - 1
                val lastChunk = myChildren[lastIndex]
                if (lastChunk !== SPACE_CHUNK) {
                    myChildren.add(SPACE_CHUNK)
                }
            }
        }

        fun addChild(element: HtmlChunk.Element) {
            myChildren.add(element)
        }
    }

    class ORDocSectionsBuilder : ORDocHtmlBuilder() {
        var myTag: String = ""
        var myHeaderCell: HtmlChunk.Element? = null

        fun addHeaderCell(tag: String) {
            myHeaderCell = DocumentationMarkup.SECTION_HEADER_CELL.child(
                HtmlChunk.text(StringUtil.toTitleCase(tag) + ":").wrapWith("p")
            )
            myChildren.add(HtmlChunk.raw("<p>"))
            myTag = tag
        }

        fun addSection() {
            val contentCell: HtmlChunk = DocumentationMarkup.SECTION_CONTENT_CELL.children(trimEndChildren(myChildren))
            myBuilder.append(HtmlChunk.tag("tr").children(myHeaderCell!!, contentCell))
            myHeaderCell = null
            myTag = ""
            myChildren.clear()
        }
    }

    companion object {
        @JvmStatic
        protected val SPACE_CHUNK: HtmlChunk = HtmlChunk.text(" ")

        @JvmStatic
        protected fun trimEndChildren(children: MutableList<HtmlChunk>): List<HtmlChunk> {
            if (!children.isEmpty()) {
                val lastIndex = children.size - 1
                val lastChunk = children[lastIndex]
                if (lastChunk === SPACE_CHUNK) {
                    children.removeAt(lastIndex)
                    return trimEndChildren(children)
                }
            }
            return children
        }
    }
}
