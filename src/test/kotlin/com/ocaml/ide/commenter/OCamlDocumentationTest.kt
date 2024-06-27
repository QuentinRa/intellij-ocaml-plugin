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

    @Test
    fun test_multiple_spaces_below() {
        configureCode("Doc.ml", "let x = 1;  \t\n  (** doc for x *)")
        assertEquals(
            "<div class=\"definition\"><b>Doc</b><p><i>let x</i></p></div><div class=\"content\"><p>doc for x</p></div>",
            getDoc(configureCode("A.ml", "Doc.x<caret>"))
        )
    }

    @Test
    fun test_type() {
        assertEquals(
            "<div class=\"definition\"><b>A</b><p><i>type t</i></p></div><div class=\"content\"><p>my type</p></div>",
            getDoc(configureCode("A.ml", "(** my type *) type t<caret> = string"))
        )
    }

    @Test
    fun test_GH_350() {
        configureCode("A.mli", "val compare : string -> string -> int\n(** compare doc *)")
        assertEquals(
            "<div class=\"definition\"><b>A</b><p><i>let compare</i></p></div><div class=\"content\"><p>compare doc</p></div>",
            getDoc(configureCode("A.ml", "let compare<caret> s1 s2 = 1"))
        )
    }
}