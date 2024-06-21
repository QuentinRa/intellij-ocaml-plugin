package com.ocaml.lang.utils;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.ocaml.ide.OCamlIdeTest;
import com.ocaml.ide.insight.OCamlAnnotResultsService;
import com.or.lang.core.psi.*;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
@Ignore
@Deprecated
public class OCamlFindFunctionTest extends OCamlIdeTest {

    private static final String FILE_NAME = "call.ml";
    private static final String FILE_NAME_ANNOT = "call.annot";

    @Override protected String getCustomTestDataPath() {
        return "com.lang.utils/find/";
    }

    private void assertFindResult(@Language("OCaml") @NotNull String code, String expectedFunctionName, int expectedIndex) {
        @Language("OCaml") String originalMlCode = loadFile(FILE_NAME);
        assertNotNull(originalMlCode);

        // create file
        myFixture.configureByText(FILE_NAME, originalMlCode);

        PsiLet elementAt = myFixture.findElementByText(code.replace("(*caret*)", ""), PsiLet.class);
        assertNotNull(elementAt);
        int i = code.indexOf("(*caret*)");
        assertNotSame("Caret missing",-1, i);
        PsiElement caret = elementAt.findElementAt(i);
        assertNotNull(caret);

        // load annotations
        OCamlAnnotResultsService annot = getProject().getService(OCamlAnnotResultsService.class);
        File annotations = new File(getTestDataPath(), FILE_NAME_ANNOT);
        annot.updateForFile(elementAt.getContainingFile().getVirtualFile().getPath(), annotations);

        Pair<PsiElement, Integer> data = new OCamlFindFunction(caret).lookForFunction(caret.getTextOffset());
        assertNotNull(data);
        assertEquals(expectedFunctionName, data.first.getText());
        System.out.println("dx:"+data.first.getTextOffset());
        assertEquals(expectedIndex, (int) data.second);
    }

    //testing with OCamlInferredSignature{name='null', kind=VALUE, type='int', file=call.ml(l:8-8, c:15-16), range=(113,114)}
    @Test
    public void testFunctionGenericWithRefBefore() {
        assertFindResult("let _ = id ref (*caret*)0", "id", 0);
    }

    @Test
    public void testFunctionGenericWithRefAfter() {
        assertFindResult("let _ = id ref 0(*caret*)", "id", 0);
    }

    @Test
    public void testFunctionWithUnitBefore() {
        assertFindResult("let _ = id (*caret*)()", "id", 0);
    }

    @Test
    public void testFunctionWithUnitIn() {
        assertFindResult("let _ = id ((*caret*))", "id", 0);
    }

    @Test
    public void testFunctionWithUnitAfter() {
        assertFindResult("let _ = id ()(*caret*)", "id", 0);
    }

    @Test
    public void testFunctionWithString() {
        assertFindResult("let _ = id (*caret*)\"something\"", "id", 0);
    }

    @Test
    public void testFunctionWithBool() {
        assertFindResult("let _ = id (*caret*)true", "id", 0);
    }

    @Test
    public void testFunctionWithFloat() {
        assertFindResult("let _ = id (*caret*)5.0", "id", 0);
    }

    @Test
    public void testFunctionTwoParamsP1() {
        assertFindResult("let _ = f 5 (*caret*)0", "f", 1);
    }

    @Test
    public void testFunctionTwoParamsP2() {
        assertFindResult("let _ = f (*caret*)5 0", "f", 0);
    }

    @Test
    public void testFunctionTwoParamsFirstScopedBeforeP1() {
        assertFindResult("let _ = f (*caret*)(0) 5", "f", 0);
    }

    @Test
    public void testFunctionTwoParamsFirstScopedBeforeP2() {
        assertFindResult("let _ = f (0) (*caret*)5", "f", 1);
    }

    @Test
    public void testFunctionTwoParamsBothScopedP1() {
        assertFindResult("let _ = f (*caret*)(0) (5)", "f", 0);
    }

    @Test
    public void testFunctionTwoParamsBothScopedAfterP2() {
        assertFindResult("let _ = f (0) (5)(*caret*)", "f", 1);
    }

    // todo: not supported / ignored test
    @Test @Ignore
    public void ignoredTestFunctionWasScoped() {
        assertFindResult("let _ = (f) (0) (*caret*)(5)", "f", 1);
    }

    @Test
    public void testFunctionWithComment() {
        assertFindResult("let _ = f 0 (* some comment *) (*caret*)5", "f", 1);
    }

    @Test
    public void testCallWithNestedCall() {
        assertFindResult("let _ = f (f 0 5) (*caret*)0", "f", 1);
    }

    @Test
    public void testCallWithNestedCallIn() {
        assertFindResult("let _ = g f (f 0 (*caret*)5) 0", "f", 1);
    }

    @Test
    public void testFunctionAsParameter() {
        assertFindResult("let _ = g max 0 (*caret*)5", "g", 2);
    }

    @Test
    public void testFunctionAsParameterWithModule() {
        assertFindResult("let _ = g Stdlib.max 0 (*caret*)5", "g", 2);
    }

    @Test
    public void testFunAsParameter() {
        assertFindResult("let _ = g (fun x y -> max x y) 0 (*caret*)5", "g", 2);
    }

    @Test
    public void testFunAsParameterIn() {
        assertFindResult("let _ = g (fun x y -> max x (*caret*)y) 0 5", "max", 1);
    }

    @Test
    public void testWithVariableFirst() {
        assertFindResult("let _ = f v_i (*caret*)0", "f", 1);
    }

    @Test
    public void testWithVariableSecond() {
        assertFindResult("let _ = f 0 (*caret*)v_i", "f", 1);
    }

    @Test
    public void testWithVariableBoth() {
        assertFindResult("let _ = f v_i (*caret*)v_i", "f", 1);
    }

    @Test
    public void testWithVariable() {
        assertFindResult("let _ = id (*caret*)v_f", "id", 0);
    }

    @Test
    public void testWithNot() { // todo: actually, it's not 1, but 0???
        // id got the type (bool -> bool) -> bool -> bool
        // meaning id f b -> b (but, actually, id is 'a -> 'a)
        assertFindResult("let _ = id not (*caret*)true", "id", 1);
    }

    @Test
    public void testWithVariantFunction() { // todo: actually, it's not 1, but 0???
        assertFindResult("let _ = id id ref (*caret*)0", "id", 1);
    }

    @Test
    public void testWithValueOfRef() { // todo: actually, it's not 1, but 0???
        assertFindResult("let _ = id !(ref id) (*caret*)0", "id", 1);
    }

    // tuples
    @Test
    public void testWithTupleOfInt() {
        assertFindResult("let _ = cpl_s (3,(*caret*)2)", "cpl_s", 0);
    }

    @Test
    public void testWithTupleOfTupleWithFunction() {
        assertFindResult("let _ = cpl_s (0, cpl_s (5, (*caret*)7))", "cpl_s", 0);
    }

    @Test
    public void testWithTupleOfTupleWithFunctionBefore() {
        assertFindResult("let _ = cpl_s (0, cpl_s (*caret*)(5, 7))", "cpl_s", 0);
    }

    @Test
    public void testWith2TuplesBeforeFunction() {
        assertFindResult("let _ = cpl2_s (0, v_i) (\"toto\",(*caret*) max 3 7)", "cpl2_s", 1);
    }

    @Test
    public void testWith2TuplesVariable() {
        assertFindResult("let _ = cpl2_s (0, (*caret*)v_i) (\"toto\", max 3 7)", "cpl2_s", 0);
    }

    // weird

    @Test
    public void testFunAsParameterOut() {
        assertFindResult("let _ = g (fun x (*caret*)y -> max x y) 0 5", "g", 0);
    }

    @Test
    public void testLetNestedWithFunction() {
        assertFindResult("let _ = max (let f x (*caret*)y = 5 in 3) 3", "max", 0);
    }

    @Test
    public void testFunParam() {
        assertFindResult("let _ = id (fun f x (*caret*)y -> f x y) f 0 5", "id", 0);
    }

    @Test
    public void testFunNameBefore() {
        assertFindResult("let _ = id (fun (*caret*)f x y -> f x y) f 0 5", "id", 0);
    }

    @Test
    public void testFunNameAfter() {
        assertFindResult("let _ = id (fun f(*caret*) x y -> f x y) f 0 5", "id", 0);
    }

    @Test // not f
    public void testNestedFunNameBefore() {
        assertFindResult("let _ = id (fun f x y -> (*caret*)f x y) f 0 5", "id", 0);
    }

    @Test // not -1
    public void testNestedFunNameAfter() {
        assertFindResult("let _ = id (fun f x y -> f(*caret*) x y) f 0 5", "f", 0);
    }

    @Test
    public void testNestedFunParam() {
        assertFindResult("let _ = id (fun f x y -> f x (*caret*)y) f 0 5", "f", 1);
    }
}
