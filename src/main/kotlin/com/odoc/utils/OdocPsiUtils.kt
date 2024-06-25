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
    private fun checkIfCommentIsInScope(element: PsiWhiteSpace): Boolean {
        val input = element.text
        var newlineCount = 0
        for (char in input) {
            if (char == '\n') {
                newlineCount++
                if (newlineCount >= 2) {
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
    private fun previousElementSkipWhitespaceIfAllowed(element: PsiElement?, preceding: Boolean) : PsiElement? {
        val sibling = if (preceding) element?.prevSibling else element?.nextSibling
        val whitespace = sibling as? PsiWhiteSpace ?: return null
        return if (checkIfCommentIsInScope(whitespace))
            if (preceding) whitespace.prevSibling else whitespace.nextSibling
        else null
    }

    // An element can be followed/preceded by many (one is confirmed to be allowed, many is my assumption)
    // normal comments that we can ignore then a documentation comment.
    // If we find such a thing, then we return the documentation comment.
    private fun findDocumentationCommentSkipNormalComments(element: PsiElement?, preceding: Boolean) : PsiComment? {
        val comment = previousElementSkipWhitespaceIfAllowed(element, preceding) as? PsiComment ?: return null
        return when (comment.elementType) {
            OCamlTypes.DOC_COMMENT -> comment
            OCamlTypes.COMMENT -> // normal comments are only allowed for preceding doc comments
                if (preceding) findDocumentationCommentSkipNormalComments(comment, true) else null
            else -> null
        }
    }

    // An element can be followed/preceded by "comment" as per <findDocumentationCommentSkipNormalComments>
    // If there is one, then we must ensure it doesn't belong to another element
    // We are checking the follower/predecessor of "target", and if it's null OR a comment, then we can use it.
    private fun findDocumentationComment(element: PsiElement, preceding: Boolean): PsiComment? {
        //println("Starting for ${element.elementType}")
        val comment = findDocumentationCommentSkipNormalComments(element, preceding) ?: return null
        //println("Previous is ${comment.elementType}")
        val target = previousElementSkipWhitespaceIfAllowed(comment, preceding)
        //println("PreviousPrevious is ${target?.elementType}")
        return if (target == null || target is PsiComment) {
            comment
        } else null
    }

    fun precedingDocumentationComment(element: PsiElement): PsiComment? = findDocumentationComment(element, true)
    fun succeedingDocumentationComment(element: PsiElement): PsiComment? = findDocumentationComment(element, false)
}