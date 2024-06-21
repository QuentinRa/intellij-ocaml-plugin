package com.ocaml.lang.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.ocaml.OCamlBaseTest;
import com.or.lang.OCamlTypes;
import com.or.lang.core.psi.PsiLet;
import org.intellij.lang.annotations.Language;
import org.junit.Test;

import java.util.Set;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class OCamlPsiUtilsTest extends OCamlBaseTest {

    // skipMeaninglessPreviousSibling

    @Test
    public void testSkipPrevious() {
        PsiElement psiElement = configureCodeWithCaret("let x = 5\n   (*something*)\n   (* something *)(*caret*)");
        PsiElement prev = OCamlPsiUtils.skipMeaninglessPreviousSibling(psiElement);
        assertInstanceOf(prev, PsiLet.class);
    }

    @Test
    public void testSkipPreviousSpaces() {
        PsiElement psiElement = configureCodeWithCaret("let x = 5\n   (*caret*)");
        PsiElement prev = OCamlPsiUtils.skipMeaninglessPreviousSibling(psiElement);
        assertInstanceOf(prev, PsiLet.class);
    }

    @Test
    public void testSkipPreviousComment() {
        PsiElement psiElement = configureCodeWithCaret("let x = 5\n(*something*)(*caret*)");
        PsiElement prev = OCamlPsiUtils.skipMeaninglessPreviousSibling(psiElement);
        assertInstanceOf(prev, PsiLet.class);
    }

    // skipMeaninglessPreviousSibling

    @Test
    public void testSkipNext() {
        PsiElement psiElement = configureCodeWithCaret("(*caret*)   (*something*)\n   (* something *)\nlet x = 5");
        PsiElement prev = OCamlPsiUtils.skipMeaninglessNextSibling(psiElement);
        assertInstanceOf(prev, PsiLet.class);
    }

    @Test
    public void testSkipNextSpaces() {
        PsiElement psiElement = configureCodeWithCaret("(*caret*)   \nlet x = 5");
        PsiElement prev = OCamlPsiUtils.skipMeaninglessNextSibling(psiElement);
        assertInstanceOf(prev, PsiLet.class);
    }

    @Test
    public void testSkipNextComment() {
        PsiElement psiElement = configureCodeWithCaret("(*caret*)(*something*)\nlet x = 5");
        PsiElement prev = OCamlPsiUtils.skipMeaninglessNextSibling(psiElement);
        assertInstanceOf(prev, PsiLet.class);
    }


    // getPsiFile

    @Test
    public void testGetPsiFile() {
        PsiFile file = myFixture.configureByText("editor.ml", "");
        PsiFile psiFile = OCamlPsiUtils.getPsiFile(myFixture.getEditor());
        assertNotNull(psiFile);
        assertSame(psiFile, file);
    }

    @Test // getNextMeaningfulSibling
    public void testGetNextMeaningfulSibling() {
        PsiElement psiElement = configureCodeWithCaret("(*caret*)( (*something*)       )");
        PsiElement rparen = OCamlPsiUtils.getNextMeaningfulSibling(psiElement, OCamlTypes.RPAREN);
        assertNotNull(rparen);
        assertEquals(OCamlTypes.RPAREN, rparen.getNode().getElementType());
        assertTrue(OCamlPsiUtils.isNextMeaningfulNextSibling(psiElement, OCamlTypes.RPAREN));
    }

    @Test // getPreviousMeaningfulSibling
    public void testGetPreviousMeaningfulSibling() {
        PsiElement psiElement = configureCodeWithCaret("( (*something*)       )(*caret*)");
        PsiElement lparen = OCamlPsiUtils.getPreviousMeaningfulSibling(psiElement, OCamlTypes.LPAREN);
        assertNotNull(lparen);
        assertEquals(OCamlTypes.LPAREN, lparen.getNode().getElementType());
    }

}
