package com.ocaml.lang.core.psi.impl;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.ocaml.OCamlBaseTest;
import com.ocaml.utils.Issue;
import com.or.lang.core.psi.impl.PsiTypeImpl;
import org.intellij.lang.annotations.Language;
import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class PsiTypeImplTest extends OCamlBaseTest {

    @Issue(id = 63, note = "option is well-know, so we shouldn't call a type like this")
    @Test
    public void testNameOfOption() {
        @Language("OCaml") String code = "type 'a option = None";
        PsiFile file = myFixture.configureByText("type.ml", code);
        PsiElement t = file.getFirstChild();
        assertInstanceOf(t, PsiTypeImpl.class);
        ItemPresentation presentation = ((PsiTypeImpl) t).getPresentation();
        assertNotNull(presentation);
        assertEquals("option", presentation.getPresentableText());
    }

}
