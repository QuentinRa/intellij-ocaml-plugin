package com.ocaml.ide.commenter

import com.intellij.lang.Language
import com.intellij.lang.LanguageDocumentation
import com.intellij.psi.PsiElement
import com.ocaml.ide.OCamlBasePlatformTestCase
import com.ocaml.language.OCamlLanguage
import com.ocaml.language.base.OCamlFileBase
import org.junit.Test

class OCamlDocumentationTest : OCamlBasePlatformTestCase() {
    private fun getQuickDoc(file: OCamlFileBase, lang: Language): String? {
        val docProvider = LanguageDocumentation.INSTANCE.forLanguage(lang)
        val resolvedElement = myFixture.elementAtCaret
        val element = file.findElementAt(myFixture.caretOffset - 1)
        return docProvider.getQuickNavigateInfo(resolvedElement, element)
    }

    private fun getDocForElement(file: OCamlFileBase, resolvedElement: PsiElement?): String? {
        val docProvider = LanguageDocumentation.INSTANCE.forLanguage(OCamlLanguage)
        val element = file.findElementAt(myFixture.caretOffset - 1)
        return docProvider.generateDoc(resolvedElement, element)
    }

    private fun getDoc(file: OCamlFileBase): String? {
        val resolvedElement = myFixture.elementAtCaret
        return getDocForElement(file, resolvedElement)
    }

    private fun assertGeneratedDocMatchesExpected(code: String, expected: String?) {
        assertEquals(
            expected,
            getDoc(configureCode("A.ml", code))
        )
    }

    @Test
    fun test_comment_before() {
        assertGeneratedDocMatchesExpected(
            "(** doc for x *)\nlet x<caret> = 1",
            "<div class=\"definition\"><b>A.x</b></div><div class=\"content\"><p>doc for x</p></div>"
        )
        assertGeneratedDocMatchesExpected(
            "(** doc for x *)\n\nlet x<caret> = 1",
            null
        )
    }

    @Test
    fun test_comment_after() {
        val expected = "<div class=\"definition\"><b>A.x</b></div><div class=\"content\"><p>doc for x</p></div>"
        assertGeneratedDocMatchesExpected("let x<caret> = 1\n(** doc for x *)", expected)
        assertGeneratedDocMatchesExpected("let x<caret> = 1\n\n(** doc for x *)", expected)
        assertGeneratedDocMatchesExpected("let x<caret> = 1\n\n\n(** doc for x *)", null)
    }

    @Test
    fun test_comment_before_and_after() {
        val expected = "<div class=\"definition\"><b>A.x</b></div><div class=\"content\"><p>doc for x</p><p>doc for x</p></div>"
        assertGeneratedDocMatchesExpected(
            "(** doc for x *)\nlet x<caret> = 1\n(** doc for x *)",
            expected
        )
        assertGeneratedDocMatchesExpected(
            "(** doc for x *)\nlet x<caret> = 1\n\n(** doc for x *)",
            expected
        )

        assertGeneratedDocMatchesExpected(
            "(** doc for x *)\n\nlet x<caret> = 1\n\n(** doc for y *)\nlet y = 2",
            null
        )

        assertGeneratedDocMatchesExpected(
            "(** some doc *)\n\nlet x<caret> = 1\n\n\n(** doc for x *)",
            null
        )
    }

    @Test
    fun test_comment_with_normal_comment() {
        assertGeneratedDocMatchesExpected(
            "(** doc for x *)\n(* x *)\nlet x<caret> = 1",
            "<div class=\"definition\"><b>A.x</b></div><div class=\"content\"><p>doc for x</p></div>"
        )
        assertGeneratedDocMatchesExpected(
            "(** doc for x *)(* x *)\nlet x<caret> = 1",
            "<div class=\"definition\"><b>A.x</b></div><div class=\"content\"><p>doc for x</p></div>"
        )
        assertGeneratedDocMatchesExpected(
            "let x<caret> = 1\n(* x *)\n(** doc for x *)",
            "<div class=\"definition\"><b>A.x</b></div><div class=\"content\"><p>doc for x</p></div>"
        )
        assertGeneratedDocMatchesExpected(
            "let x<caret> = 1\n(* x *)(** doc for x *)",
            "<div class=\"definition\"><b>A.x</b></div><div class=\"content\"><p>doc for x</p></div>"
        )
    }

    @Test
    fun test_with_annotation_in_between() {
        assertGeneratedDocMatchesExpected(
            "let x<caret> = 1\n[@@ocaml.deprecated]\n(** doc for x *)",
            "<div class=\"definition\"><b>A.x</b></div><div class=\"content\"><p>doc for x</p></div>"
        )
        assertGeneratedDocMatchesExpected(
            "let x<caret> = 1\n[@@ocaml.deprecated](** doc for x *)",
            "<div class=\"definition\"><b>A.x</b></div><div class=\"content\"><p>doc for x</p></div>"
        )
    }

    @Test
    fun test_ambiguous_comment() {
        // READ THIS
        // Ambiguous comments are associated to both (tested with Odoc)
        // While actually according to the doc they are associated with the first one (Foo)
        // So, that why, the comment is ambiguous, as it is from the documentation
        // Enjoy!
        val code = "(** Comment for foo *)\n" +
                "let foo : string = \"\"\n" +
                "(** This comment is associated to foo and not to bar. *)\n" +
                "let bar : string = \"\"\n" +
                "(** This comment is associated to bar. *)"
        assertGeneratedDocMatchesExpected(
            code.replace("let foo", "let foo<caret>"),
            "<div class=\"definition\"><b>A.foo</b></div><div class=\"content\"><p>Comment for foo</p><p>This comment is associated to foo and not to bar.</p></div>"
        )
        assertGeneratedDocMatchesExpected(
            code.replace("let bar", "let bar<caret>"),
            "<div class=\"definition\"><b>A.bar</b></div><div class=\"content\"><p>This comment is associated to foo and not to bar.</p><p>This comment is associated to bar.</p></div>"
        )
    }

//    @Test
//    fun test_multiple_spaces_below() {
//        configureCode("Doc.ml", "let x = 1;  \t\n  (** doc for x *)")
//        assertEquals(
//            "<div class=\"definition\"><b>Doc</b><p><i>let x</i></p></div><div class=\"content\"><p>doc for x</p></div>",
//            getDoc(configureCode("A.ml", "Doc.x<caret>"))
//        )
//    }
//
//    @Test
//    fun test_type() {
//        assertEquals(
//            "<div class=\"definition\"><b>A</b><p><i>type t</i></p></div><div class=\"content\"><p>my type</p></div>",
//            getDoc(configureCode("A.ml", "(** my type *) type t<caret> = string"))
//        )
//    }
//
//    @Test
//    fun test_GH_350() {
//        configureCode("A.mli", "val compare : string -> string -> int\n(** compare doc *)")
//        assertEquals(
//            "<div class=\"definition\"><b>A</b><p><i>let compare</i></p></div><div class=\"content\"><p>compare doc</p></div>",
//            getDoc(configureCode("A.ml", "let compare<caret> s1 s2 = 1"))
//        )
//    }
}