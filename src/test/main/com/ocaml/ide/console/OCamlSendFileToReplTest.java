package com.ocaml.ide.console;

import com.ocaml.ide.OCamlIdeTest;
import com.ocaml.ide.actions.editor.run.OCamlRunFileREPLAction;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class OCamlSendFileToReplTest extends OCamlIdeTest {

    private void assertFileWasSent(@Language("OCaml") @NotNull String code, String ... expected) {
        myFixture.configureByText("file.ml", code);

        OCamlFakeRunner runner = new OCamlFakeRunner();
        OCamlRunFileREPLAction.doAction(myFixture.getEditor(), myFixture.getProject(), runner);

        if (expected != null && expected.length != 0) {
            assertContainsElements(runner.commands, expected);
            assertSize(expected.length, runner.commands);
        } else {
            assertEmpty(runner.commands);
        }
    }

    @Test
    public void testEmpty() {
        assertFileWasSent("");
    }

    @Test
    public void testSpaces() {
        assertFileWasSent("    \n  ");
    }

    @Test
    public void testNormalFile() {
        assertFileWasSent("let x = 6\n" +
                "let y = 6.0\n" +
                "let f x y = float_of_int x +. y\n" +
                "let _ = f x y",
                "let x = 6", "let y = 6.0", "let f x y = float_of_int x +. y", "let _ = f x y");
    }

    @Test
    public void testAnd() {
        assertFileWasSent("let x = 6 and y = 7", "let x = 6 and y = 7");
    }

    @Test
    public void testAndAnd() {
        assertFileWasSent("let x = 6 and y = 7\nlet x = 6 and y = 7",
                "let x = 6 and y = 7", "let x = 6 and y = 7");
    }

    @Test
    public void testNotAVariable() {
        assertFileWasSent("\"Hello, World!\"");
    }

    @Test
    public void testException() {
        assertFileWasSent("exception E", "exception E");
    }

    @Test
    public void testType() {
        assertFileWasSent("type t", "type t");
    }

    @Test
    public void testModule() {
        assertFileWasSent("module type T = sig end\n" +
                "\n" +
                "module T_Impl : T = struct end",
                "module type T = sig end", "module T_Impl : T = struct end");
    }

    @Test
    public void testStructure() {
        assertFileWasSent("type 'a zipper = {\n" +
                "\tleft : 'a list;\n" +
                "\tright : 'a list;\n" +
                "}\n" +
                "\n" +
                "let my_zipper = {left= []; right= [] }",
                "type 'a zipper = {\n" + "\tleft : 'a list;\n" +
                "\tright : 'a list;\n" + "}", "let my_zipper = {left= []; right= [] }");
    }

    @Test
    public void testClass() {
        assertFileWasSent("class type name = object\n" +
                "\tmethod name : int * int\n" +
                "end\n" +
                "\n" +
                "\n" +
                "class stack_of_ints =\n" +
                "    object (self)\n" +
                "      val mutable the_list = ([] : int list)\n" +
                "    end",
                "class type name = object\n" +
                        "\tmethod name : int * int\n" +
                        "end", "class stack_of_ints =\n" +
                        "    object (self)\n" +
                        "      val mutable the_list = ([] : int list)\n" +
                        "    end");
    }

    @Test
    public void testComment() {
        assertFileWasSent("(* comment 1 *)\n" +
                "\n" +
                "(* comment 2 *)");
    }

}
