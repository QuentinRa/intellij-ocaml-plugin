package com.ocaml.ide.console;

import com.intellij.psi.PsiElement;
import com.ocaml.ide.OCamlIdeTest;
import com.ocaml.utils.editor.ExtendedEditorActionUtil;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class OCamlAutoSelectStatementTest extends OCamlIdeTest {

    private void assertWasAutoSelected(@Language("OCaml") @NotNull String code, String ... expected) {
        configureCodeWithCaret(code);
        // auto select
        ArrayList<PsiElement> elements = ExtendedEditorActionUtil.autoSelectStatements(myFixture.getEditor());

        if (expected != null && expected.length != 0) {
            assertNotNull(elements);
            List<String> expectedList = Arrays.asList(expected);
            // ensure that every returned element, is inside expected
            for (PsiElement e: elements)
                assertTrue(expectedList.contains(e.toString()));
            assertSize(expectedList.size(), elements);
        } else {
            assertTrue(elements == null || elements.size() == 0);
        }
    }

    @Test
    public void testNestedLet() {
        assertWasAutoSelected("let (//) x y =\n" +
                "  let res(*caret*) = if y = 0 then Error \"Division by zero\" else Ok (x / y)\n" +
                "  in res", "Let File.(//)", "Let File.(//).UNKNOWN");
    }

    @Test
    public void testNestedLet2() {
        assertWasAutoSelected("let (//) x y =\n" +
                "  let _ = if y = 0 then Error \"Division by zero\"\n" +
                "  else Ok (x / y)\n" +
                "  in let z(*caret*) = 2 in z",
                "Let File.(//)", "Let File.(//).UNKNOWN");
    }

    @Test
    public void testNestedFun() {
        assertWasAutoSelected("let distance x y : int =\n" +
                "  let diff(*caret*) f s c = match f with\n" +
                "    | [] -> c\n" +
                "    | _ -> 4\n" +
                "    in diff x y 0",
                "Let File.distance", "Let File.distance.UNKNOWN");
    }

    @Test
    public void testModule() {
        assertWasAutoSelected("module X2 = struct\n" +
                "type t(*caret*) = int\n" +
                "let compare = compare\n" +
                "end", "Module File.X2", "Let File.X2.UNKNOWN");
    }

    @Test
    public void testModuleType() {
        assertWasAutoSelected("module type C = sig\n" +
                "    type t\n" +
                "    val r(*caret*) : int\n" +
                "    val k : int * int\n" +
                "    val z : float\n" +
                "end", "Module File.C", "Let File.C.UNKNOWN");
    }

    @Test
    public void testClassType() {
        assertWasAutoSelected("class type name = object\n" +
                "\tmethod name(*caret*) : int * int\n" +
                "end", "Class File.name", "Let File.name.UNKNOWN");
    }

    @Test
    public void testClass() {
        assertWasAutoSelected("class stack_of_ints =\n" +
                "    object (self)\n" +
                "      val mutable the_list(*caret*) = ([] : int list)\n" +
                "    end",
                "Class File.stack_of_ints", "Let File.stack_of_ints.UNKNOWN");
    }

    // Issues

    @Test
    public void test_Issue85And() {
        assertWasAutoSelected("let x = 5 and y(*caret*) = 7", "Let File.UNKNOWN");
        assertWasAutoSelected("let x = 5(*caret*) and y = 7", "Let File.UNKNOWN");
    }

}
