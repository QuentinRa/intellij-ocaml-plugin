package com.odoc.utils

import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.elementType
import com.ocaml.language.psi.OCamlTypes

object OdocPsiUtils {

    /**
     * Dune comments are not associated to a target if there is a blank line.
     * @param element true if there is at least one blank line
     */
    private fun checkIfCommentIsInScope(element: PsiWhiteSpace, allow_one_blank_line: Boolean = false): Boolean {
        val input = element.text
        val target = if (allow_one_blank_line) 2 else 1
        var newlineCount = 0
        for (char in input) {
            if (char == '\n') {
                newlineCount++
                if (newlineCount > target) {
                    return false
                }
            }
        }
        return true
    }

    // We are crawling from <element> and looking at the comments
    // But there is a restriction of NO BLANK LINE
    // e.g.
    //
    // (** a comment **)
    //
    // let x = 5 (* the comment above is not associated with x *)
    //
    // This function is called to ensure we are stopping crawling when we see a blank line
    // It basically crawl the whitespace (if any) and pass it to "checkIfCommentIsInScope"
    private fun previousElementSkipWhitespaceIfAllowed(element: PsiElement?, preceding: Boolean, bypass: Boolean = false) : PsiElement? {
        val sibling = if (preceding) element?.prevSibling else element?.nextSibling
        if (sibling is PsiComment) return sibling
        val whitespace = sibling as? PsiWhiteSpace ?: return null
        return if (checkIfCommentIsInScope(whitespace, bypass))
            if (preceding) whitespace.prevSibling else whitespace.nextSibling
        else null
    }

    // An element can be followed/preceded by many (one is confirmed to be allowed, many is my assumption)
    // normal comments that we can ignore then a documentation comment.
    // If we find such a thing, then we return the documentation comment.
    private fun findDocumentationCommentSkipNormalComments(element: PsiElement?, preceding: Boolean, bypass: Boolean = false) : PsiComment? {
        val comment = previousElementSkipWhitespaceIfAllowed(element, preceding, bypass) as? PsiComment ?: return null
        return when (comment.elementType) {
            OCamlTypes.DOC_COMMENT -> comment
            OCamlTypes.COMMENT -> // normal comments are only allowed for preceding doc comments
                // The doc was not followed again, we can have normal comments both above and below
                findDocumentationCommentSkipNormalComments(comment, preceding)
            OCamlTypes.ANNOTATION -> // annotations are allowed for succeeding comments
                findDocumentationCommentSkipNormalComments(comment, preceding)
            else -> null
        }
    }

    // An element can be followed/preceded by "comment" as per <findDocumentationCommentSkipNormalComments>
    // If there is one, then we must ensure it doesn't belong to another element
    // We are checking the follower/predecessor of "target", and if it's null OR a comment, then we can use it.
    private fun findDocumentationComment(element: PsiElement, preceding: Boolean): PsiComment? {
        val word = if (preceding) "Previous" else "Next"
        println("Starting for ${element.elementType}")
        var comment : PsiComment? = findDocumentationCommentSkipNormalComments(element, preceding)
        if (comment != null || preceding) {
            println("$word is ${comment?.elementType}")
            return comment
        }
        comment = findDocumentationCommentSkipNormalComments(element, preceding, true) ?: return null
        println("$word is ${comment.elementType} (bypass)")

        // There was some comment, alone, below our element, so we can take it
        val target = previousElementSkipWhitespaceIfAllowed(comment, preceding)
        println("$word$word is ${target?.elementType} (bypass)")
        return if (target == null || target is PsiComment) {
            comment
        } else null
    }

    fun precedingDocumentationComment(element: PsiElement): PsiComment? = findDocumentationComment(element, true)
    fun succeedingDocumentationComment(element: PsiElement): PsiComment? = findDocumentationComment(element, false)
}