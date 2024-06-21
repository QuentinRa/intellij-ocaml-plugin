package com.ocaml.ide.console;

import com.ocaml.ide.OCamlIdeTest;
import com.ocaml.ide.actions.editor.run.OCamlRunSelection;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class OCamlSendToReplAutoTest extends OCamlIdeTest {

    private void assertWasSend(@Language("OCaml") @NotNull String code) {
        assertWasSend(code, code.replace(OCAML_CARET, ""));
    }

    private void assertWasSend(@Language("OCaml") @NotNull String code, String expectedCodeSent) {
        myFixture.configureByText("file.ml", code.replace(OCAML_CARET, EXPECTED_CARET));
        int caretOffset = myFixture.getCaretOffset();
        assertNotSame(-1, caretOffset);

        OCamlFakeRunner runner = new OCamlFakeRunner();
        OCamlRunSelection.doAction(myFixture.getEditor(), runner);
        assertSize(1, runner.commands);
        assertEquals(expectedCodeSent, runner.commands.get(0));
    }

    @Test
    public void testTypeAfter() {
        assertWasSend("type t(*caret*)");
    }

    @Test
    public void testTypeIn() {
        assertWasSend("type(*caret*) t");
    }

    @Test
    public void testTypeBefore() {
        assertWasSend("(*caret*)type t");
    }

    @Test
    public void testInTypeDeclaration() {
        assertWasSend("type nucleotide = A | C | G(*caret*) | T");
    }

    @Test
    public void testAndInFirst() {
        assertWasSend("let x = 5(*caret*) and y = 3");
    }

    @Test
    public void testAndInSecond() {
        assertWasSend("let x = 5 and (*caret*)y = 3");
    }

    @Test
    public void testAndInThird() {
        assertWasSend("let x = 5 and y = 3 and (*caret*)z = 9");
    }

    @Test
    public void testInList() {
        assertWasSend("let u = [3; 4; (*caret*)5]");
    }

    @Test
    public void testInCouple() {
        assertWasSend("let (x, y) = (5, (*caret*)3)");
    }

    @Test
    public void testFunction() {
        assertWasSend("let f1(*caret*) y = 5");
    }

    @Test
    public void testException() {
        assertWasSend("exception (*caret*)E1");
    }

    @Test
    public void testExceptionInDeclaration() {
        assertWasSend("exception E2 of int * int");
    }

    @Test
    public void testMultilinesFunction() {
        assertWasSend("let (//) x y(*caret*) =\n" +
                "  if y = 0 then Error \"Division by zero\"\n" +
                "  else Ok (x / y)");
    }

    @Test
    public void testMultilinesFunctionIn() {
        assertWasSend("let (//) x y =\n" +
                "  if y = 0(*caret*) then Error \"Division by zero\"\n" +
                "  else Ok (x / y)");
    }

    @Test
    public void testModule() {
        assertWasSend("module X2 = struct(*caret*)\n" +
                "type t = int\n" + "let compare = compare\n" +
                "end");
    }

    @Test
    public void testModuleIn() {
        // is not returning "type t = int", but the whole module
        // as this is the first choice of the selection menu
        assertWasSend("module X2 = struct\n" +
                "type t =(*caret*) int\n" + "let compare = compare\n" +
                "end");
    }

    // issues

    @Test
    public void test_issue57_EndOfFile() {
        // fixed, cursor after the last quote -> CTRL-ENTER failed
        @Language("OCaml") String code = "let x = \"Hello, World!@.\"(*caret*)";
        assertWasSend(code);
    }

}
