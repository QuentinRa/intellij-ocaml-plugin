package com.ocaml.ide.console;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Pair;
import com.ocaml.OCamlBaseTest;
import com.ocaml.ide.console.debug.OCamlREPLOutputParser;
import com.ocaml.ide.console.debug.groups.TreeElementGroupKind;
import com.ocaml.ide.console.debug.groups.elements.*;
import com.ocaml.sdk.repl.OCamlREPLConstants;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.List;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class OCamlREPLOutputParserTest extends OCamlBaseTest {

    private void assertVariable(String message, String expectedValue, String expectedText, String expectedLocation) {
        List<Pair<OCamlTreeElement, TreeElementGroupKind>> r = assertResult(message);
        assertSize(1, r);
        Pair<OCamlTreeElement, TreeElementGroupKind> e = r.get(0);
        assertElement(e, expectedValue, expectedText, expectedLocation, OCamlVariableElement.class);
    }

    private void assertFunction(String message, String expectedText, String expectedLocation) {
        List<Pair<OCamlTreeElement, TreeElementGroupKind>> r = assertResult(message);
        assertSize(1, r);
        Pair<OCamlTreeElement, TreeElementGroupKind> e = r.get(0);
        assertElement(e, OCamlREPLConstants.FUN, expectedText, expectedLocation, OCamlFunctionElement.class);
    }

    private void assertType(String message, String expectedValue, String expectedText) {
        List<Pair<OCamlTreeElement, TreeElementGroupKind>> r = assertResult(message);
        assertSize(1, r);
        Pair<OCamlTreeElement, TreeElementGroupKind> e = r.get(0);
        assertElement(e, expectedValue, expectedText, null, OCamlTypeElement.class);
    }

    private void assertException(String message, String expectedText) {
        List<Pair<OCamlTreeElement, TreeElementGroupKind>> r = assertResult(message);
        assertSize(1, r);
        Pair<OCamlTreeElement, TreeElementGroupKind> e = r.get(0);
        assertElement(e, null, expectedText, null, OCamlExceptionElement.class);
    }

    private @NotNull List<Pair<OCamlTreeElement, TreeElementGroupKind>> assertResult(String message) {
        // get the result
        List<Pair<OCamlTreeElement, TreeElementGroupKind>> res = OCamlREPLOutputParser.parse(message);
        assertNotNull(res);
        return res;
    }

    private void assertVariableElement(@NotNull Pair<OCamlTreeElement, TreeElementGroupKind> parse,
                                       String expectedValue,
                                       String expectedText, String expectedLocation) {
        assertElement(parse, expectedValue, expectedText, expectedLocation, OCamlVariableElement.class);
    }

    private void assertFunctionElement(@NotNull Pair<OCamlTreeElement, TreeElementGroupKind> parse,
                                           String expectedText, String expectedLocation) {
        assertElement(parse, OCamlREPLConstants.FUN, expectedText, expectedLocation, OCamlFunctionElement.class);
    }

    private void assertModuleElement(@NotNull Pair<OCamlTreeElement, TreeElementGroupKind> parse,
                                           String expectedText) {
        assertElement(parse, null, expectedText, null, OCamlModuleElement.class);
    }

    private <T> void assertElement(@NotNull Pair<OCamlTreeElement, TreeElementGroupKind> parse,
                                   String expectedValue, String expectedText,
                                   String expectedLocation, Class<T> aClass) {
        // get the string
        OCamlTreeElement e = parse.first;
        assertInstanceOf(e, aClass);
        if (expectedValue == null) {
           assertTrue(e.isValueNull());
        } else {
            assertEquals(expectedValue, e.getValue());
        }

        ItemPresentation presentation = e.getPresentation();
        assertEquals(expectedText, presentation.getPresentableText());
        assertEquals(expectedLocation, presentation.getLocationString());
    }

    @Test
    public void testSimpleVariable() {
        assertVariable(
                "val hw : string = \"Hello, World!@.\"",
                "\"Hello, World!@.\"", "hw = \"Hello, World!@.\"",
                "string");
    }

    @Test
    public void testVariableList() {
        assertVariable("val l : int list = [3; 4; 5]",
                "[3; 4; 5]",
                "l = [3; 4; 5]",
                "int list");
    }

    @Test
    public void testVariableConstructor() {
        assertVariable("val t : nucleotide option = Some A",
                "Some A",
                "t = Some A",
                "nucleotide option");
    }

    @Test
    public void testWithNewLine() {
        assertVariable("val x : int\n= 5",
                "5",
                "x = 5",
                "int");
    }

    @Test
    public void testReallyLongVariable() {
        assertVariable("val big_list : int list =\n" +
                        "[3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3;\n" +
                        "4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4;\n" +
                        "5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5;\n" +
                        "3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3;\n" +
                        "4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4;\n" +
                        "5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5;\n" +
                        "3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3;\n" +
                        "4; 5; 3; 4; 5]",
                "[3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; " +
                        "4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; " +
                        "5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; " +
                        "3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; " +
                        "4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; " +
                        "5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; " +
                        "3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; " +
                        "4; 5; 3; 4; 5]",
                "big_list = [3; 4; 5; ...; 3; 4; 5]",
                "int list");
    }

    @Test
    public void testVariableAnd() {
        List<Pair<OCamlTreeElement, TreeElementGroupKind>> res =
                assertResult("val x : int = 5\n" + "val y : int = 3");
        assertSize(2, res);
        assertVariableElement(res.get(0), "5", "x = 5", "int");
        assertVariableElement(res.get(1), "3", "y = 3", "int");
    }

    @Test
    public void testFunction() {
        assertFunction("val f1 : 'a -> int = <fun>", "f1 = <fun>", "'a -> int");
    }

    @Test
    public void testFunctionWithLongType() {
        assertFunction("val f2 : int -> int -> int -> int -> int -> int = <fun>",
                "f2 = <fun>",
                "int -> int -> int -> int -> int -> int");
    }

    @Test
    public void testFunctionWithNewLine() {
        assertFunction("val f3 : float -> ('a -> float) -> 'a -> float -> float -> float -> float =\n" +
                        "<fun>",
                "f3 = <fun>",
                "float -> ('a -> float) -> 'a -> float -> float -> float -> float");
    }

    @Test
    public void testFunctionAnd() {
        List<Pair<OCamlTreeElement, TreeElementGroupKind>> res =
                assertResult("val f4 : 'a -> int = <fun>\n" + "val f5 : 'a -> int = <fun>");
        assertSize(2, res);
        assertFunctionElement(res.get(0), "f4 = <fun>", "'a -> int");
        assertFunctionElement(res.get(1), "f5 = <fun>", "'a -> int");
    }

    @Test
    public void testFunctionAndVariable() {
        List<Pair<OCamlTreeElement, TreeElementGroupKind>> res =
                assertResult("val f6 : 'a -> int = <fun>\n" + "val v : int = 5");
        assertSize(2, res);
        assertFunctionElement(res.get(0), "f6 = <fun>", "'a -> int");
        assertVariableElement(res.get(1), "5", "v = 5", "int");
    }

    @Test
    public void testFunctionWithLabels() {
        assertFunction("val f7 : x:int -> y:int -> int = <fun>",
                "f7 = <fun>",
                "x:int -> y:int -> int");
    }

    @Test
    public void testSimpleType() {
        assertType("type t", null, "t");
    }

    @Test
    public void testType() {
        assertType("type nucleotide = A | C | G | T",
                "A | C | G | T",
                "nucleotide = A | C | G | T");
    }

    @Test
    public void testLongType() {
        assertType("type acide =\n" +
                        "Ala\n" +
                        "| Arg\n" +
                        "| Asn\n" +
                        "| Asp\n" +
                        "| Cys\n" +
                        "| Glu\n" +
                        "| Gln\n" +
                        "| Gly\n" +
                        "| His\n" +
                        "| Ile\n" +
                        "| Leu\n" +
                        "| Lys\n" +
                        "| Phe\n" +
                        "| Pro\n" +
                        "| Ser\n" +
                        "| Thr\n" +
                        "| Trp\n" +
                        "| Tyr\n" +
                        "| Val\n" +
                        "| START\n" +
                        "| STOP",
                "Ala | Arg | Asn | Asp | Cys | Glu | Gln | Gly | His | Ile | Leu | Lys | Phe | Pro | Ser | Thr | Trp | Tyr | Val | START | STOP",
                "acide = Ala | Arg ...ART | STOP");
    }

    @Test
    public void testSimpleException() {
        assertException("exception E1", "E1");
    }

    @Test
    public void testException() {
        assertException("exception E2 of int * int", "E2 of int * int");
    }

    @Test
    public void testEmptyModule() {
        List<Pair<OCamlTreeElement, TreeElementGroupKind>> res = assertResult("module X1 : sig  end");
        assertModuleElement(res.get(0), "X1");
    }

    @Test
    public void testSimpleModule() {
        List<Pair<OCamlTreeElement, TreeElementGroupKind>> res = assertResult("module X2 : sig type t = int val compare : 'a -> 'a -> int end");
        assertModuleElement(res.get(0), "X2");
    }

    @Test
    public void testModule() {
        String module = "module My_Set :\n" +
                "sig\n" +
                "type elt = X2.t\n" +
                "type t = Set.Make(X2).t\n" +
                "val empty : t\n" +
                "val is_empty : t -> bool\n" +
                "val mem : elt -> t -> bool\n" +
                "val add : elt -> t -> t\n" +
                "end";
        List<Pair<OCamlTreeElement, TreeElementGroupKind>> res = assertResult(module);
        assertModuleElement(res.get(0), "My_Set");
//        assertFunctionElement(res.get(2), "empty", "t");
//        assertFunctionElement(res.get(3), "is_empty", "t -> bool");
//        assertFunctionElement(res.get(4), "mem", "elt -> t -> bool");
//        assertFunctionElement(res.get(5), "add", "elt -> t -> t");
    }
}
