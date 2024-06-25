package com.ocaml.ide.commenter

import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocCommentBase
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.ocaml.language.base.OCamlFileBase
import com.ocaml.language.psi.OCamlLetBindings
import com.odoc.lang.OdocConverter
import com.odoc.utils.OdocPsiUtils
import java.util.function.Consumer

class OCamlDocumentationProvider : DocumentationProvider {
    override fun findDocComment(file: PsiFile, range: TextRange): PsiDocCommentBase? {
        return super.findDocComment(file, range)
    }

    // CTRL+Q/hover
    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        //println("Generate doc for ${element?.text}")
        // For now, nested elements are ignored
        val (root, parent) = when (val p = element?.parent) {
            is OCamlLetBindings -> p.parent to p
            else -> return null
        }
        if (root !is OCamlFileBase) return null

        val preceding = OdocPsiUtils.precedingDocumentationComment(parent)
        val following = OdocPsiUtils.succeedingDocumentationComment(parent)
        //println("Prev comment is ${preceding?.text}")
        //println("Next comment is ${following?.text}")

        val converter = OdocConverter()
        var text = ""
        preceding?.text?.let { text += converter.convert(it) }
        following?.text?.let { text += converter.convert(it) }
        return if (text == "") null else text
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
}