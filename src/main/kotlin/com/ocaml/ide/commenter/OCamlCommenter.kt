package com.ocaml.ide.commenter

import com.intellij.codeInsight.generation.CommenterDataHolder
import com.intellij.codeInsight.generation.CommenterWithLineSuffix
import com.intellij.codeInsight.generation.SelfManagingCommenter
import com.intellij.codeInsight.generation.SelfManagingCommenterUtil
import com.intellij.lang.Commenter
import com.intellij.lang.CustomUncommenter
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.Couple
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiFile
import com.intellij.util.text.CharArrayUtil

// Source: https://github.com/giraud/reasonml-idea-plugin/blob/master/src/main/java/com/reason/ide/comment/OclCommenter.java
// Revision: 7c77496991c53cde57c6d4b0ebf711e0c1bd06c8
class OCamlCommenter : Commenter, CommenterWithLineSuffix, CustomUncommenter, SelfManagingCommenter<CommenterData> {
    override fun getLineCommentPrefix(): String = "(*"
    override fun getLineCommentSuffix(): String = "*)"
    override fun getBlockCommentPrefix(): String  = "(*"
    override fun getBlockCommentSuffix(): String  = "*)"
    override fun getCommentedBlockCommentPrefix(): String  = "(*"
    override fun getCommentedBlockCommentSuffix(): String  = "*)"
    override fun getCommentPrefix(line: Int, document: Document, data: CommenterData): String = "(*"
    override fun getBlockCommentPrefix(selectionStart: Int, document: Document, data: CommenterData): String = "(*"
    override fun getBlockCommentSuffix(selectionEnd: Int, document: Document, data: CommenterData): String = "*)"

    override fun createLineCommentingState(
        startLine: Int,
        endLine: Int,
        document: Document,
        file: PsiFile
    ): CommenterData {
        return CommenterData(startLine, endLine)
    }
    override fun createBlockCommentingState(
        selectionStart: Int,
        selectionEnd: Int,
        document: Document,
        file: PsiFile
    ): CommenterData {
        val startLine = StringUtil.offsetToLineNumber(document.charsSequence, selectionStart)
        val endLine = StringUtil.offsetToLineNumber(document.charsSequence, selectionEnd)
        return CommenterData(startLine, endLine)
    }

    override fun commentLine(line: Int, offset: Int, document: Document, data: CommenterData) {
        val lineEndOffset = document.getLineEndOffset(line)
        SelfManagingCommenterUtil.insertBlockComment(
            offset,
            lineEndOffset,
            document,
            getLineCommentPrefix(),
            getLineCommentSuffix()
        )
    }

    override fun uncommentLine(line: Int, offset: Int, document: Document, data: CommenterData) {
        val chars = document.charsSequence
        val lineEndOffset = document.getLineEndOffset(line)
        val textEndOffset = CharArrayUtil.shiftBackward(chars, lineEndOffset - 1, " \t")
        SelfManagingCommenterUtil.uncommentBlockComment(offset, textEndOffset + 1, document, "(*", "*)")
    }

    override fun isLineCommented(line: Int, offset: Int, document: Document, data: CommenterData): Boolean {
        val charSequence = document.charsSequence.subSequence(offset, offset + 2)
        return charSequence.toString() == blockCommentPrefix
    }

    override fun getBlockCommentRange(
        selectionStart: Int,
        selectionEnd: Int,
        document: Document,
        data: CommenterData
    ): TextRange? {
        return SelfManagingCommenterUtil.getBlockCommentRange(selectionStart, selectionEnd, document, "(*", "*)")
    }

    override fun uncommentBlockComment(startOffset: Int, endOffset: Int, document: Document, data: CommenterData) {
        val chars = document.charsSequence
        val startChar = chars[startOffset + 2]
        val startHasLF = startChar == '\n'
        val startHasSpace = startChar == ' '
        val endChar = chars[endOffset - 1 - 2]
        val endHasLF = endChar == '\n'
        val endHasSpace = endChar == ' '
        val start = if (startHasLF) "(*\n" else if (startHasSpace) "(* " else "(*"
        val end = if (endHasLF) "\n*)" else if (endHasSpace) " *)" else "*)"
        SelfManagingCommenterUtil.uncommentBlockComment(startOffset, endOffset, document, start, end)
    }

    override fun insertBlockComment(
        startOffset: Int,
        endOffset: Int,
        document: Document,
        data: CommenterData
    ): TextRange {
        val chars = document.charsSequence
        val startHasLF = chars[startOffset] == '\n'
        val endHasLF = chars[endOffset - 1] == '\n'
        val hasLF = data.endLine > data.startLine
        val startComment = if (startHasLF) "(*" else if (hasLF) "(*\n" else "(* "
        val endComment = if (endHasLF) "*)\n" else " *)"
        return SelfManagingCommenterUtil.insertBlockComment(startOffset, endOffset, document, startComment, endComment)
    }

    override fun findMaximumCommentedRange(text: CharSequence): TextRange? {
        var commentedRange: TextRange? = null

        // trim start & end
        var selectionStart = 0
        selectionStart = CharArrayUtil.shiftForward(text, selectionStart, " \t\n")
        var selectionEnd = text.length - 1
        selectionEnd = CharArrayUtil.shiftBackward(text, selectionEnd, " \t\n") + 1

        // Find how many distinct comments in text
        val commentStart = CharArrayUtil.regionMatches(text, selectionStart, "(*")
        if (commentStart) {
            var commentCount = 0
            var nestedComment = 0
            for (i in selectionStart until selectionEnd) {
                val c = text[i]
                if (c == '(') {
                    val c2 = text[i + 1]
                    if (c2 == '*') {
                        nestedComment++
                    }
                } else if (c == '*') {
                    val c2 = text[i + 1]
                    if (c2 == ')') {
                        nestedComment--
                        if (nestedComment == 0) {
                            commentCount++
                        }
                    }
                }
            }
            if (commentCount == 1 && selectionEnd - selectionStart >= 2 + 2 && CharArrayUtil.regionMatches(
                    text,
                    selectionEnd - 2,
                    "*)"
                )
            ) {
                commentedRange = TextRange(selectionStart, selectionEnd)
            }
        }
        return commentedRange
    }

    override fun getCommentRangesToDelete(text: CharSequence): Collection<Couple<TextRange>> {
        val ranges: MutableCollection<Couple<TextRange>> = ArrayList()
        // should use nearest after all pairs (* *)
        val start = Utilities.getNearest(text as String)
        val prefixRange = expandRange(text, start, start + 2)
        val end = text.lastIndexOf("*)")
        val suffixRange = expandRange(text, end, end + 2)
        ranges.add(Couple.of(prefixRange, suffixRange))
        return ranges
    }

    private fun expandRange(chars: CharSequence, delOffset1: Int, delOffset2: Int): TextRange {
        var newDelOffset1 = delOffset1
        var newDelOffset2 = delOffset2
        val offset1 = CharArrayUtil.shiftBackward(chars, newDelOffset1 - 1, " \t")
        if (offset1 < 0 || chars[offset1] == '\n' || chars[offset1] == '\r') {
            val offset2 = CharArrayUtil.shiftForward(chars, newDelOffset2, " \t")
            if (offset2 == chars.length || chars[offset2] == '\r' || chars[offset2] == '\n') {
                newDelOffset1 = if (offset1 < 0) offset1 + 1 else offset1
                if (offset2 < chars.length) {
                    newDelOffset2 = offset2 + 1
                    if (chars[offset2] == '\r' && offset2 + 1 < chars.length && chars[offset2 + 1] == '\n') {
                        newDelOffset2++
                    }
                }
            }
        }
        return TextRange(newDelOffset1, newDelOffset2)
    }

    internal object Utilities {
        @JvmStatic
        fun getNearest(text: String): Int {
            val result = text.indexOf("(*")
            return if (result == -1) text.length else result
        }
    }

}

data class CommenterData(val startLine: Int, val endLine: Int) : CommenterDataHolder()