package com.ocaml.ide.insight.parameters;

import com.intellij.psi.PsiElement;
import com.ocaml.ide.OCamlIdeTest;
import com.ocaml.ide.insight.OCamlParameterInfo;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class ParameterInfoTest extends OCamlIdeTest {

    @Override protected String getCustomTestDataPath() {
        return "com.ocaml.ide.insight/parameters/";
    }

    private void doTest(@NotNull String text, int expectedIndex, String ... values) {
        // load file
        PsiElement caret = configureCodeWithCaret("param.ml", text);
        // load annotations
        configureAnnotResultsService(caret, "param.annot");

        FakeCreateParameterInfoContext context = new FakeCreateParameterInfoContext(
                getProject(), caret
        );

        // generate result
        new OCamlParameterInfo().findElementForParameterInfo(context);

        // get result
        if (expectedIndex == -1) { // empty
            assertNull(context.toShow);
            return;
        }
        // or, not
        String message = "Failed for "+text;
        assertNotNull(message, context.toShow);
        assertSize(1, context.toShow);
        assertInstanceOf(context.toShow[0], OCamlParameterInfo.ParameterInfoArgumentList.class);
        var p = (OCamlParameterInfo.ParameterInfoArgumentList) context.toShow[0];

        // assert names
        List<String> expected = Arrays.asList(values);
        assertEquals(message, expected, p.names);

        // assert index
        assertEquals(message+". Expected index, ", expectedIndex, p.currentArgumentIndex);
    }

    @Test
    public void testBeforeFunctionFull() {
        doTest("let _ =(*caret*) max 5 7", -1);
    }

    @Test
    public void testInvalidContext() {
        doTest("let _(*caret*) = max 5 7", -1);
    }

    @Test
    public void testAfterFirstFull() {
        doTest("let _ = max 5(*caret*) 7", 1,"int", "int");
        doTest("let _ = max 5 7(*caret*)", 2,"int", "int");
    }

    @Test
    public void testAfterFunctionFull() {
        doTest("let _ = max(*caret*) 5 7", 0,"int", "int");
    }

    @Test
    public void testCoupleFull() {
        doTest("let _ = max (5 + max 5 7) (7 + 3)(*caret*)", 2,"int", "int");
        doTest("let _ = max (5 + max 5 7)(*caret*) (7 + 3)", 1,"int", "int");
        doTest("let _ = max(*caret*) (5 + max 5 7) (7 + 3)", 0,"int", "int");
    }

    @Test
    public void testInCoupleFull() {
        doTest("let _ = max (5 + max 5 7) (7 + 3(*caret*))", 2, "int", "int");
        doTest("let _ = max (5 + max 5 7(*caret*)) (7 + 3)", 2, "int", "int");
        doTest("let _ = max (5 +(*caret*) max 5 7) (7 + 3)", 1, "int", "int");
        doTest("let _ = max (5(*caret*) + max 5 7) (7 + 3)", 1, "int", "int");
    }

    @Test
    public void testFunctionAsArgument() {
        String[] types = { "int -> int -> int", "int", "int" };
        doTest("let id v(*caret*) = v", -1);
        doTest("let _ = id max 0(*caret*) 5", 2, types);
        doTest("let _ = id max (*caret*)0 5", 1, types);
        doTest("let _ = id m(*caret*)ax 0 5", 1, types);
        doTest("let _ = id (*caret*)max 0 5", 0, types);
        doTest("let _ = id max 0 5(*caret*)", 3, types);
    }

    @Test
    public void testLabelsOnlyNormalOrder() {
        String[] types = { "x:int", "y:float" };
        doTest("let _ = f ~x:3 ~y:2.0(*caret*)", 2, types);
        doTest("let _ = f ~x:3(*caret*) ~y:2.0", 1, types);
        doTest("let _ = f (*caret*)~x:3 ~y:2.0", 0, types);

        // x and y are inverted
        types = new String[]{"[y:float]", "[x:int]"};
        doTest("let _ = f ~y:2.0 ~x:3(*caret*)", 2, types);
        doTest("let _ = f ~y:2.0 (*caret*)~x:3", 1, types);
        doTest("let _ = f (*caret*)~y:2.0 ~x:3", 0, types);
    }

    // why so many tests? because the code is <s>shit</s> a mess.
    @Test
    public void testLabelsAndNormal() {
        // normal order
        String[] types = { "x:int", "y:float", "int" };
        doTest("let _ = f ~x:3 ~y:2.0 3(*caret*)", 3, types);
        doTest("let _ = f ~x:3 ~y:2.0(*caret*) 3", 2, types);
        doTest("let _ = f ~x:3(*caret*) ~y:2.0 3", 1, types);
        doTest("let _ = f (*caret*)~x:3 ~y:2.0 3", 0, types);

        // we got a third parameter, that is not a label
        types = new String[]{"[int]", "[x:int]", "[y:float]"};
        doTest("let _ = f 3 ~x:3 ~y:2.0(*caret*)", 3, types);
        doTest("let _ = f 3 ~x:3 (*caret*)~y:2.0", 2, types);
        doTest("let _ = f 3 (*caret*)~x:3 ~y:2.0", 1, types);
        doTest("let _ = f (*caret*)3 ~x:3 ~y:2.0", 0, types);
    }

    // why so many tests? because the code is <s>shit</s> a mess.
    @Test
    public void testOptional() {
        // step missing
        String[] types = { "?step:int", "int" };
        doTest("let _ = bump ~step:2 3(*caret*)", 2, types);
        doTest("let _ = bump ~step:2(*caret*) 3", 1, types);
        doTest("let _ = bump (*caret*)~step:2 3", 0, types);

        // normal
        types = new String[]{"[int]", "[?step:int]"};
        doTest("let _ = bump 3(*caret*)", 1, types);
        doTest("let _ = bump(*caret*) 3", 0, types);

        // inverted
        doTest("let _ = bump 3 ~step: 2(*caret*)", 2, types);
        doTest("let _ = bump 3(*caret*) ~step: 2", 1, types);
        doTest("let _ = bump (*caret*)3 ~step: 2", 0, types);

        // advanced
        String[] t1 = { "?s1:int", "?s2:int", "int" };
        String[] t2 = { "[int]", "[?s1:int]", "[?s2:int]" };
        String[] t3 = { "?s1:int", "[int]", "[?s2:int]" };

        doTest("let _ = bump 3 ~s1: 2 ~s2: 2(*caret*)", 3, t2);
        doTest("let _ = bump 3 ~s1: 2(*caret*) ~s2: 2", 2, t2);
        doTest("let _ = bump 3(*caret*) ~s1: 2 ~s2: 2", 1, t2);
        doTest("let _ = bump (*caret*)3 ~s1: 2 ~s2: 2", 0, t2);

        doTest("let _ = bump ~s1: 2 ~s2: 2 5(*caret*)", 3, t1);
        doTest("let _ = bump ~s1: 2 ~s2: 2(*caret*) 5", 2, t1);
        doTest("let _ = bump ~s1: 2(*caret*) ~s2: 2 5", 1, t1);
        doTest("let _ = bump (*caret*)~s1: 2 ~s2: 2 5", 0, t1);

        doTest("let _ = bump ~s1: 2 55 ~s2: 2(*caret*)", 3, t3);
        doTest("let _ = bump ~s1: 2 55(*caret*) ~s2: 2", 2, t3);
        doTest("let _ = bump ~s1: 2(*caret*) 55 ~s2: 2", 1, t3);
        doTest("let _ = bump (*caret*)~s1: 2 55 ~s2: 2", 0, t3);

        doTest("let _ = bump(*caret*)\n", 0, t1);
        doTest("let _ = bump (*caret*)42", 0, t2);
        doTest("let _ = bump 42(*caret*)", 1, t2);
    }

    @Test
    public void testLabelSpecial(){
        String[] types = { "x:int", "y:float" };
        doTest("let _ = f ~x:3 ~y:(*caret*)2.0", 2, types);
        doTest("let _ = f ~x:(*caret*)3 ~y:2.0", 1, types);

        doTest("let _ = f ~x:3 ~y(*caret*):2.0", 2, types);
        doTest("let _ = f ~x(*caret*):3 ~y:2.0", 1, types);

        doTest("let _ = f ~x:3 ~(*caret*)y:2.0", 2, types);
        doTest("let _ = f ~(*caret*)x:3 ~y:2.0", 1, types);

        // spaces
        doTest("let _ = f ~x:3 ~y: 2.0(*caret*)", 2, types);
        doTest("let _ = f ~x: 3 ~y:2.0(*caret*)", 2, types);
        doTest("let _ = f ~x: 3 ~y: 2.0(*caret*)", 2, types);
    }

    @Test
    public void testMultilines(){
        String[] types = { "[int]", "[?s1:int]", "[?s2:int]" };
        doTest("let _ = bump 3 ~s1: 2\n" + "\n" + "(*caret*)\n" + "~s2: 2",
                2, types);
        doTest("let _ = bump 3 ~s1: 2\n" + "(* ELRaphik loves Mari *)\n" + "(*caret*)\n" + "~s2: 2",
                2, types);
    }
}
