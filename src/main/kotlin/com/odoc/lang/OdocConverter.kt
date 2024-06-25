package com.odoc.lang

import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.util.text.HtmlBuilder
import com.intellij.openapi.util.text.HtmlChunk
import com.intellij.psi.TokenType
import com.intellij.util.containers.Stack
import com.odoc.language.lexer._OdocLexer
import com.odoc.utils.logs.OdocLogger
import java.io.IOException

/**
 * @see com.intellij.codeInsight.documentation.DocumentationManagerUtil
 *
 * @see DocumentationMarkup
 *
 * @see com.intellij.codeInsight.documentation.DocumentationManagerProtocol
 */
class OdocConverter : ORDocConverter() {
    private val myLexer = _OdocLexer()

    override fun convert(text: String): HtmlBuilder {
        myLexer.reset(text, 0, text.length, _OdocLexer.YYINITIAL)

        val builders = Stack<ORDocHtmlBuilder>()
        builders.add(ORDocHtmlBuilder())
        var currentBuilder = builders.peek()
        var advanced = false

        try {
            var tokenType = myLexer.advance()
            while (tokenType != null) {
                if (LOG.isTraceEnabled) {
                    LOG.trace(tokenType.toString() + " : " + myLexer.yytext())
                }

                // We have not produced the sections table, and it's the end of the comment
                if (currentBuilder is ORDocSectionsBuilder && tokenType === OdocTypes.COMMENT_END) {
                    val sectionsBuilder = builders.pop() as ORDocSectionsBuilder
                    currentBuilder = builders.peek()

                    trimEndChildren(sectionsBuilder.myChildren)
                    sectionsBuilder.myChildren.add(HtmlChunk.raw("</p>"))
                    sectionsBuilder.addSection()

                    currentBuilder.myBuilder.append(sectionsBuilder.myBuilder.wrapWith(DocumentationMarkup.SECTIONS_TABLE))
                    currentBuilder.myChildren.clear()
                }

                if (tokenType === OdocTypes.COMMENT_START || tokenType === OdocTypes.COMMENT_END) {
                    // skip
                } else if (tokenType === OdocTypes.CODE) {
                    val yyValue = extract(1, 1, myLexer.yytext())
                    currentBuilder.addChild(
                        HtmlChunk.raw(DocumentationMarkup.GRAYED_START + yyValue + DocumentationMarkup.GRAYED_END)
                            .wrapWith("code")
                    )
                } else if (tokenType === OdocTypes.BOLD) {
                    val yyValue = extract(2, 1, myLexer.yytext())
                    currentBuilder.addChild(HtmlChunk.text(yyValue).wrapWith("b"))
                } else if (tokenType === OdocTypes.ITALIC) {
                    val yyValue = extract(2, 1, myLexer.yytext())
                    currentBuilder.addChild(HtmlChunk.text(yyValue).italic())
                } else if (tokenType === OdocTypes.EMPHASIS) {
                    val yyValue = extract(2, 1, myLexer.yytext())
                    currentBuilder.addChild(HtmlChunk.text(yyValue).wrapWith("em"))
                } else if (tokenType === OdocTypes.PRE) {
                    val yyValue = extractRaw(2, 2, myLexer.yytext())
                    currentBuilder.addChild(HtmlChunk.raw(yyValue).wrapWith("code").wrapWith("pre"))
                } else if (tokenType === OdocTypes.O_LIST) {
                    currentBuilder.appendChildren(true)
                    val listBuilder = TagHtmlBuilder("ol")
                    builders.add(listBuilder)
                    currentBuilder = listBuilder
                } else if (tokenType === OdocTypes.U_LIST) {
                    currentBuilder.appendChildren(true)
                    val listBuilder = TagHtmlBuilder("ul")
                    builders.add(listBuilder)
                    currentBuilder = listBuilder
                } else if (tokenType === OdocTypes.LIST_ITEM_START) {
                    currentBuilder.appendChildren(false)
                    val listBuilder = TagHtmlBuilder("li")
                    builders.add(listBuilder)
                    currentBuilder = listBuilder
                } else if (tokenType === OdocTypes.SECTION) {
                    currentBuilder.appendChildren(true)
                    val tag = "h" + extract(1, 0, myLexer.yytext())
                    val listBuilder = TagHtmlBuilder(tag)
                    builders.add(listBuilder)
                    currentBuilder = listBuilder
                } else if (tokenType === OdocTypes.RBRACE) {
                    val builder = if (builders.empty()) null else builders.pop()
                    currentBuilder = builders.peek()
                    if (builder is TagHtmlBuilder) {
                        val tagBuilder = builder
                        tagBuilder.appendChildren(false)
                        currentBuilder.addChild(tagBuilder.myBuilder.wrapWith(tagBuilder.myTag))
                        if (tagBuilder.myTag.startsWith("h")) {
                            // a title
                            currentBuilder.appendChildren(false)
                        }
                    }
                } else if (tokenType === OdocTypes.LINK_START) {
                    // consume url
                    val sbUrl = StringBuilder()
                    tokenType = myLexer.advance()
                    while (tokenType != null && tokenType !== OdocTypes.RBRACE) {
                        sbUrl.append(myLexer.yytext())
                        tokenType = myLexer.advance()
                    }
                    if (tokenType === OdocTypes.RBRACE) {
                        tokenType = myLexer.advance()
                        // consume text
                        val sbText = StringBuilder()
                        while (tokenType != null && tokenType !== OdocTypes.RBRACE) {
                            if (tokenType !== OdocTypes.NEW_LINE) {
                                sbText.append(myLexer.yytext())
                            }
                            tokenType = myLexer.advance()
                        }
                        if (tokenType === OdocTypes.RBRACE) {
                            currentBuilder.addChild(HtmlChunk.link(sbUrl.toString(), sbText.toString()))
                        }
                    }
                } else if (tokenType === OdocTypes.TAG) {
                    val yyValue = extract(1, 0, myLexer.yytext())
                    if (currentBuilder is ORDocSectionsBuilder) {
                        val sectionsBuilder = currentBuilder
                        trimEndChildren(sectionsBuilder.myChildren)
                        if (sectionsBuilder.myTag == yyValue) {
                            sectionsBuilder.myChildren.add(HtmlChunk.raw("</p><p>"))
                        } else {
                            sectionsBuilder.myChildren.add(HtmlChunk.raw("</p>"))
                            sectionsBuilder.addSection()

                            sectionsBuilder.addHeaderCell(yyValue)
                        }
                        tokenType = skipWhiteSpace(myLexer)
                        advanced = true
                    } else {
                        currentBuilder.appendChildren(true)

                        val sectionsBuilder = ORDocSectionsBuilder()
                        sectionsBuilder.addHeaderCell(yyValue)
                        tokenType = skipWhiteSpace(myLexer)
                        advanced = true

                        builders.add(sectionsBuilder)
                        currentBuilder = sectionsBuilder
                    }
                } else if (tokenType === OdocTypes.NEW_LINE) {
                    if (currentBuilder !is ORDocSectionsBuilder) {
                        // \n\n is a new line
                        tokenType = myLexer.advance()
                        val spaceAfterNewLine = tokenType === TokenType.WHITE_SPACE
                        if (spaceAfterNewLine) {
                            tokenType = skipWhiteSpace(myLexer)
                        }

                        if (tokenType === OdocTypes.NEW_LINE) {
                            currentBuilder.appendChildren(true)
                        } else {
                            currentBuilder.addSpace()
                        }

                        advanced = true
                    }
                } else {
                    val yyValue = myLexer.yytext().toString()

                    val isSpace = tokenType === TokenType.WHITE_SPACE
                    if (!(isSpace && currentBuilder.myChildren.isEmpty())) {
                        currentBuilder.myChildren.add(if (isSpace) SPACE_CHUNK else HtmlChunk.text(yyValue))
                    }
                }

                if (advanced) {
                    advanced = false
                } else {
                    tokenType = myLexer.advance()
                }
            }
        } catch (e: IOException) {
            LOG.error("Error during ODoc parsing", e)
        }

        currentBuilder.appendChildren(true)

        if (LOG.isTraceEnabled) {
            LOG.trace("HTML: " + currentBuilder.myBuilder)
        }

        return currentBuilder.myBuilder
    }

    internal class TagHtmlBuilder(val myTag: String) : ORDocHtmlBuilder()
    companion object {
        private val LOG = OdocLogger.instance
    }
}
