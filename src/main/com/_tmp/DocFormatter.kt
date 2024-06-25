package com.ocaml.ide.commenter._tmp

import com.intellij.openapi.util.text.HtmlBuilder
import com.intellij.openapi.util.text.HtmlChunk
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNamedElement
import com.or.ide.files.FileBase
import java.util.*

internal object DocFormatter {
    private val LOG: Log = Log.create("doc.formatter")

    fun format(file: PsiFile, element: PsiElement, lang: ORLanguageProperties?, text: String): String {
        if (file is FileBase) {
            val source: FileBase = file as FileBase

            // Definition
            val definitionBuilder = HtmlBuilder()

            var path: String = source.getModuleName()
            if (element is PsiQualifiedPathElement) {
                path = Joiner.join(".", (element as PsiQualifiedPathElement).getPath())
            }
            definitionBuilder.append(HtmlChunk.text(path).bold())

            if (element is PsiNamedElement) {
                val className =
                    element.javaClass.simpleName.substring(3).replace("Impl", "").lowercase(Locale.getDefault())
                val name = element.name
                if (name != null) {
                    definitionBuilder.append(HtmlChunk.raw("<p><i>"))
                    definitionBuilder.append(HtmlChunk.text("$className $name"))

                    if (element is PsiSignatureElement) {
                        val signature: PsiSignature = (element as PsiSignatureElement).getSignature()
                        if (signature != null) {
                            definitionBuilder.append(HtmlChunk.text(" : "))
                                .append(HtmlChunk.text(signature.asText(lang)).wrapWith("code"))
                        }
                    }
                }
                definitionBuilder.append(HtmlChunk.raw("</i></p>"))
            }

            // Content
            val contentBuilder = HtmlBuilder()

            /* if (isDeprecated()) {
                contentBuilder
                      .append(HtmlChunk.text(xxx).bold().wrapWith(HtmlChunk.font("#" + ColorUtil.toHex(JBColor.RED))))
                      .append(HtmlChunk.br());
            } */
            val converter: OdocConverter = OdocConverter()
            contentBuilder.append(converter.convert(text))

            // final render
            val builder = HtmlBuilder()
            builder.append(definitionBuilder.wrapWith(DocumentationMarkup.DEFINITION_ELEMENT))
            builder.append(contentBuilder.wrapWith(DocumentationMarkup.CONTENT_ELEMENT))

            if (LOG.isDebugEnabled()) {
                LOG.debug(builder.toString())
            }

            return builder.toString()
        }
        return text
    }


    fun escapeCodeForHtml(code: PsiElement?): String {
        if (code == null) {
            return ""
        }

        return escapeCodeForHtml(code.text)!!
    }

    fun escapeCodeForHtml(code: String?): String? {
        return code?.replace
        "<".toRegex(), "&lt;").replace
        ">".toRegex(), "&gt;")
    }
}
