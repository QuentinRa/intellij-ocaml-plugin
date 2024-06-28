package com.ocaml.ide.commenter

import com.intellij.codeInsight.generation.actions.CommentByBlockCommentAction
import com.intellij.codeInsight.generation.actions.CommentByLineCommentAction
import com.ocaml.ide.OCamlBasePlatformTestCase
import org.junit.Test

// From https://github.com/giraud/reasonml-idea-plugin/blob/master/src/test/java/com/reason/ide/comment/OclCommenterTest.java
// Revision: 3f738ddfa632e686a7139504fcae7a1660eab0e2
class OCamlCommenterTest : OCamlBasePlatformTestCase() {

    @Test
    fun test_line_commenter() {
        configureCode("A.ml", "  comment<caret>")
        val action = CommentByLineCommentAction()
        action.actionPerformedImpl(project, myFixture.editor)
        myFixture.checkResult("(*  comment*)")
        action.actionPerformedImpl(project, myFixture.editor)
        myFixture.checkResult("  comment")
    }

    @Test
    fun test_line_uncommenter() {
        configureCode("A.ml", "  (* comment *)<caret>")
        val action = CommentByLineCommentAction()
        action.actionPerformedImpl(project, myFixture.editor)
        myFixture.checkResult("   comment ")
    }

    @Test
    fun test_line_uncommenter_02() {
        configureCode("A.ml", "  (*comment*)<caret>")
        val action = CommentByLineCommentAction()
        action.actionPerformedImpl(project, myFixture.editor)
        myFixture.checkResult("  comment")
    }

    @Test
    fun test_line_uncommenter_03() {
        configureCode("A.ml", "\ntest\n(*    comment    *)    <caret>")
        val action = CommentByLineCommentAction()
        action.actionPerformedImpl(project, myFixture.editor)
        myFixture.checkResult("\ntest\n    comment        ")
    }

    /*--
    length: 17 / commented: 0-17 / deleteStart: 0-3 / deleteEnd: 14-17
    !(*
      x (* y *)   =>   x (* y *)
    *)!
    <caret>
    --*/
    @Test
    fun test_GH_27() {
        configureCode("A.ml", "<selection>(*\n  x (* y *)\n*)</selection>\n<caret>")
        val action = CommentByBlockCommentAction()
        action.actionPerformedImpl(project, myFixture.editor)
        myFixture.checkResult("  x (* y *)\n")
    }

    /*--
    length: 18 / commented: null
                           (*
    ...(* x *)             (* x *)
    y               =>     y
    (* z *)...             (* z *)
                           *)
    --*/
    @Test
    fun test_GH_27b() {
        configureCode("A.ml", "<selection>(* x *)\ny\n(* z *)\n</selection><caret>")
        val commentAction = CommentByBlockCommentAction()
        commentAction.actionPerformedImpl(project, myFixture.editor)
        myFixture.checkResult("(*\n(* x *)\ny\n(* z *)\n*)\n")
    }

    @Test
    fun test_GH_27c() {
        configureCode("A.ml", "<selection>(* x *)\n(* z *)\n</selection><caret>")
        val commentAction = CommentByBlockCommentAction()
        commentAction.actionPerformedImpl(project, myFixture.editor)
        myFixture.checkResult("(*\n(* x *)\n(* z *)\n*)\n")
        assertEquals(19, myFixture.caretOffset)
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/319
    @Test
    fun test_GH_319() {
        configureCode("A.ml", "line with (<caret>")
        val a = CommentByLineCommentAction()
        a.actionPerformedImpl(project, myFixture.editor)
        myFixture.checkResult("(*line with (*)")
    }

    @Test
    fun test_GH_319_b() {
        configureCode("A.ml", " line with ( <caret>")
        val a = CommentByLineCommentAction()
        a.actionPerformedImpl(project, myFixture.editor)
        myFixture.checkResult("(* line with ( *)")
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/411
    @Test
    fun test_GH_411_comment() {
        configureCode("A.ml", "Infile <selection>of</selection><caret> dirpath")
        val commentAction = CommentByBlockCommentAction()
        commentAction.actionPerformedImpl(project, myFixture.editor)
        myFixture.checkResult("Infile (* of *) dirpath")
        assertEquals(11, myFixture.caretOffset)
    }

    @Test
    fun test_GH_411_uncomment() {
        configureCode("A.ml", "Infile <selection>(* of *)</selection><caret> dirpath")
        val commentAction = CommentByBlockCommentAction()
        commentAction.actionPerformedImpl(project, myFixture.editor)
        myFixture.checkResult("Infile of dirpath")
        assertEquals(9, myFixture.caretOffset)
    }

}