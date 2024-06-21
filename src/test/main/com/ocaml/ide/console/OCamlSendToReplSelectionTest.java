package com.ocaml.ide.console;

import com.ocaml.ide.OCamlIdeTest;
import com.ocaml.ide.actions.editor.run.OCamlRunSelection;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class OCamlSendToReplSelectionTest extends OCamlIdeTest {

    private void assertWasSend(@Language("OCaml") @NotNull String code) {
        assertWasSend(code, null);
    }

    private void assertWasSend(@Language("OCaml") @NotNull String code, @Nullable String expectedCode) {
        configureCodeWithCaret(code);
        String selectedText = myFixture.getEditor().getSelectionModel().getSelectedText();
        assertNotNull(selectedText);
        int s = code.indexOf(OCAML_SELECT_START) + OCAML_SELECT_START.length();
        int e = code.indexOf(OCAML_SELECT_END);
        String expectedSelected = code.substring(s, e);

        assertEquals(expectedSelected, selectedText);

        OCamlFakeRunner runner = new OCamlFakeRunner();
        OCamlRunSelection.doAction(myFixture.getEditor(), runner);
        assertSize(1, runner.commands);
        // override the default value used to check the selected code
        if (expectedCode != null) expectedSelected = expectedCode;
        assertEquals(expectedSelected, runner.commands.get(0));
    }

    @Test
    public void testLet() {
        assertWasSend("(*selection*)let _ = \"Hello, World\"(*/selection*)");
    }

    @Test
    public void testValue() {
        assertWasSend("let _ = (*selection*)\"Hello, World\"(*/selection*)");
    }

    @Test
    public void testNestedLet() {
        assertWasSend(
                "let x = (*selection*)let y = 9(*/selection*)"
        );
    }

    @Test
    public void testWithAComment() {
        assertWasSend("let x = (*selection*)(* hw *) 9(*/selection*)");
    }

    @Test
    public void testWithComments() {
        assertWasSend("let x = (*selection*)(* hw *) 9 (* hw *) (* hw *)(*/selection*)");
    }

    @Test
    public void testMultilines() {
        assertWasSend("let x = (*selection*)5\n" + "let y = 5\n" + "let z = 5(*/selection*)");
    }

    @Test
    public void testComplexMultilines() {
        assertWasSend("let x = (*selection*)\n\n\n5\n" + "let y = 5\n" + "let z = 5(*/selection*)");
    }

}
