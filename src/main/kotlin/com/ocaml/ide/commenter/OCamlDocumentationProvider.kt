package com.ocaml.ide.commenter

import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.HtmlBuilder
import com.intellij.openapi.util.text.HtmlChunk
import com.intellij.psi.PsiDocCommentBase
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.ocaml.language.base.OCamlFileBase
import com.ocaml.language.psi.OCamlLetBindings
import com.ocaml.language.psi.OCamlTypeDefinition
import com.ocaml.language.psi.OCamlValueDescription
import com.ocaml.language.psi.api.OCamlQualifiedNamedElement
import com.ocaml.language.psi.mixin.OCamlValuePathBindingMixin
import com.ocaml.language.psi.mixin.OCamlValuePathReference
import com.odoc.lang.OdocConverter
import com.odoc.utils.OdocPsiUtils
import java.util.function.Consumer

class OCamlDocumentationProvider : DocumentationProvider {
    override fun findDocComment(file: PsiFile, range: TextRange): PsiDocCommentBase? {
        return super.findDocComment(file, range)
    }

    // CTRL+Q/hover
    override fun generateDoc(base: PsiElement?, originalElement: PsiElement?): String? {
        val element = when (base) {
            is OCamlValuePathBindingMixin -> OCamlValuePathReference(base).resolveFirst()
            else -> base
        }

//        println("Generate doc for ${element?.text}/${element?.elementType}/${element?.parent?.elementType}")
        // For now, nested elements are ignored
        val (root, parent) = when (val p = element?.parent) {
            is OCamlLetBindings -> p.parent to p
            is OCamlValueDescription -> p.parent to p
            is OCamlTypeDefinition -> p.parent to p
            else -> return null
        }
//        println("Root: $root")
//        println("Parent: $parent")
        if (root !is OCamlFileBase) return null

        val preceding = OdocPsiUtils.precedingDocumentationComment(parent)
        val following = OdocPsiUtils.succeedingDocumentationComment(parent)
//        println("Prev comment is ${preceding?.text}")
//        println("Next comment is ${following?.text}")

        val converter = OdocConverter()
        var text = ""
        preceding?.text?.let { text += converter.convert(it) }
        following?.text?.let { text += converter.convert(it) }
        return if (text == "") null else formatDocumentation(element, text)
    }

    // Pretty render
    override fun generateRenderedDoc(comment: PsiDocCommentBase): String? {
        println(comment)
        return super.generateRenderedDoc(comment)
    }

    // Call on every file
    override fun collectDocComments(file: PsiFile, sink: Consumer<in PsiDocCommentBase>) {
        if (file !is OCamlFileBase) return
    }

    // JavaDocInfoGenerator
    // JavaDocumentationProvider
    private fun formatDocumentation(element: PsiElement, text: String) : String {
        val target = element as? OCamlQualifiedNamedElement ?: return text
        val targetQualifiedName = target.qualifiedName ?: return text

        val definitionBuilder = HtmlBuilder()
        //definitionBuilder.append(HtmlChunk.tag("icon").attr("src", "AllIcons.Nodes.Package"))
        definitionBuilder.append(HtmlChunk.text(" $targetQualifiedName").bold())

        val contentBuilder = HtmlBuilder()
        contentBuilder.appendRaw(text)

        val builder = HtmlBuilder()
        builder.append(definitionBuilder.wrapWith(DocumentationMarkup.DEFINITION_ELEMENT))
        builder.append(contentBuilder.wrapWith(DocumentationMarkup.CONTENT_ELEMENT))
        return builder.toString()
    }
}