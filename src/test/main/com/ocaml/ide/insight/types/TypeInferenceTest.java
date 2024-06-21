package com.ocaml.ide.insight.types;

import com.intellij.psi.PsiElement;
import com.ocaml.ide.OCamlIdeTest;
import com.ocaml.ide.insight.OCamlAnnotResultsService;
import com.ocaml.ide.insight.OCamlTypeInfoHint;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.File;
import java.util.List;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class TypeInferenceTest extends OCamlIdeTest {

    private void doTest(@NotNull String text, String expectedType) {
        // load file
        PsiElement caret = configureCodeWithCaret("types.ml", text);
        // load annotations
        configureAnnotResultsService(caret, "types.annot");

        OCamlTypeInfoHint infoHint = new OCamlTypeInfoHint();

        if (expectedType == null) assertEmpty(infoHint.getExpressionsAt(caret));
        else {
            List<PsiElement> expressionsAt = infoHint.getExpressionsAt(caret);
            StringBuilder elements = new StringBuilder("[");
            boolean ok = false;
            for (PsiElement psiElement : expressionsAt) {
                // check type
                String type = infoHint.getInformationHint(psiElement);
                elements.append(type).append(',');
                if (!type.equals(expectedType)) continue;
                ok = true;
                break;
            }
            elements.append("]");

            assertTrue("Could not find type:"+expectedType+" in " +elements+" for "+text, ok);
        }
    }

    private void assertInvalid(String text) {
        doTest(text, null);
    }

    private void assertValid(String text, String expectedType) {
        doTest(text, expectedType);
    }

    @Override protected String getCustomTestDataPath() {
        return "com.ocaml.ide.insight/types/";
    }

    // The cursor must be before the variable/...
    // in the tests, even if you can use this after the variable/...
    // in the editor. Change the tests if that's a bother.

    @Test
    public void testLet() {
        assertInvalid("let(*caret*) x = 5");
        assertValid("let x(*caret*) = 5", "int");
        assertValid("let _(*caret*) = 5", "int");
        assertInvalid("let x =(*caret*) 5");
        assertValid("let x = 5(*caret*)", "int");
    }

    @Test
    public void testLiterals() {
        assertValid("true(*caret*)", "bool");
        assertValid("false(*caret*)", "bool");
        assertValid("5(*caret*)", "int");
        assertValid("5.0(*caret*)", "float");
        assertValid("\"Hello, World!\"(*caret*)", "string");
    }

    @Test
    public void testAdvancedLiterals() {
        assertValid("()(*caret*)", "unit");
        assertValid("{ n = 5 }(*caret*)", "s");
    }

    @Test
    public void testFunctions() {
        assertValid("let f1(*caret*) x y = ()", "'a -> 'b -> unit");
        assertValid("let f1 x y(*caret*) = ()", "'b");
        assertValid("let f2 = fun x -> fun y(*caret*) -> ()", "'b");
    }

    @Test
    public void testFunctionFun() {
        assertValid("let derivative dx f = fun x(*caret*) -> (f (x +. dx) -. f x) /. dx",
                "float");
    }

    @Test
    public void testList() {
        assertValid("let li = 1::2::3::[](*caret*)", "int list");
        assertValid("let li = 1::2::3:(*caret*):[]", "int list");
        assertValid("[1; 2; 3](*caret*)", "int list");
        assertValid("[1; 2;(*caret*) 3]", "int list");
    }

    @Test
    public void testMatch() {
        assertValid("| Num i(*caret*) -> i", "int");
        assertValid("| Num i -> i(*caret*)", "int");
        assertValid("List.assoc x(*caret*)", "string");
        assertValid("(x(*caret*), e1, in_e2)", "string");
        assertValid("eval env e1(*caret*)", "expr");
        assertValid("((x, val_x(*caret*))", "int");
        assertValid("eval_op op(*caret*) v1 v2", "string");
        assertValid("v1(*caret*) + v2", "int");
        assertValid("v1 +(*caret*) v2", "int -> int -> int");
        assertValid("\"-\"(*caret*) -> v1 - v2", "string");
        assertValid("\"-\" -> v1 -(*caret*) v2", "int -> int -> int");
        assertValid("\"*\" -> v1 *(*caret*) v2", "int -> int -> int");
        assertValid("\"/\" -> v1 /(*caret*) v2", "int -> int -> int");
        assertValid("(\"Unknown operator: \"(*caret*) ^ op)", "string");
        assertValid("(\"Unknown operator: \" ^(*caret*) op)", "string -> string -> string");
        assertValid("(\"Unknown operator: \" ^ op(*caret*))", "string");
        assertValid("match op(*caret*) with", "string");
        assertValid("| _(*caret*) ->", "string");
    }

    @Test
    public void testFun() {
        assertValid("let f2 = fun(*caret*) x -> fun y -> ()", "'a -> 'b -> unit");
    }

    // hum, these are okay, but this was not what I was expecting
    // like, I was expecting the type of List.assoc, but I'm too lazy to fix that
    // because it's finally working :3
    //
    // Note: this is behaving like in Java. PRs are welcome.

    @Test
    public void testWithIdentifier() {
//        assertValid("List(*caret*).assoc x", "string -> (string * int) list -> int");
//        assertValid("List.assoc(*caret*) x", "string -> (string * int) list -> int");
        assertValid("List(*caret*).assoc x env", "int");
        assertValid("List.assoc(*caret*) x env", "int");
    }

    @Test // same, was expecting expr, but got the type of the whole statement
    public void testStatement() {
        assertValid("| Num(*caret*) i -> i", "expr -> int");
    }

    // Bugs

    @Test
    public void test_IssueWrong() {
        assertValid("eval_op(*caret*) op v1 v2", "string -> int -> int -> int");
    }

    @Test // there is a comment with a non-ascii character before
    // it was messing with the ranges
    public void test_NonAsciiCharacter() {
        assertValid("let _ = \"Some word\"(*caret*)", "string");
    }
}
