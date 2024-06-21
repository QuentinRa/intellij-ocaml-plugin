package com.ocaml.sdk.annot;

import com.intellij.build.FilePosition;
import com.ocaml.OCamlBaseTest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.util.ArrayList;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class OcamlAnnotParserTest extends OCamlBaseTest {

    /**
     * A method to match 'rincewind' output format, with the output.
     * @param input passed to the annot parser
     * @param expectedOutput 'rincewind' output format (edit: Li was added).
     *                       One per line (add \n).
     */
    private void assertParserResult(String input, @Nullable String expectedOutput) {
        ArrayList<OCamlInferredSignature> res = new OCamlAnnotParser(input).get();
        // we are not expecting any result
        if (expectedOutput == null) {
            assertEmpty(res);
            return;
        }

        String[] lines = expectedOutput.split("\n");
        assertSize(lines.length, res);
        for (int i = 0; i < lines.length; i++) {
            OCamlInferredSignature e = res.get(i);
            String result = null;
            switch (e.kind) {
                case UNKNOWN: fail(); break;
                case VALUE:
                    result = "Li|"+parsePosition(e.position)+"|"+e.type;
                    break;
                case VARIABLE:
                    result = "Va|"+parsePosition(e.position)+"|"+e.name+"|"+e.type;
                    break;
                case MODULE:
                    result = "Md|"+parsePosition(e.position)+"|"+e.name;
                    break;
            }
            assertNotNull(result);
            assertEquals(lines[i], result);
        }
    }

    private @NotNull String parsePosition(@NotNull FilePosition position) {
        assertNotNull(position.getFile());
        return position.getStartLine()+"."+position.getStartColumn()+","
                +position.getEndLine()+"."+position.getEndColumn();
    }

    @Test
    public void testLet() {
        assertParserResult("\"test.ml\" 1 0 4 \"test.ml\" 1 0 5\n" +
                "type(\n" +
                "  int\n" +
                ")\n" +
                "ident(\n" +
                "  def x \"test.ml\" 1 0 9 \"test.ml\" 0 0 -1\n" +
                ")\n" +
                "\"test.ml\" 1 0 8 \"test.ml\" 1 0 9\n" +
                "type(\n" +
                "  int\n" +
                ")", "Va|1.4,1.5|x|int\n" +
                "Li|1.8,1.9|int");
    }

    @Test
    public void testLetLet() {
        assertParserResult("\"test.ml\" 1 0 4 \"test.ml\" 1 0 5\n" +
                        "type(\n" +
                        "  int\n" +
                        ")\n" +
                        "ident(\n" +
                        "  def x \"test.ml\" 2 10 10 \"test.ml\" 0 0 -1\n" +
                        ")\n" +
                        "\"test.ml\" 1 0 8 \"test.ml\" 1 0 9\n" +
                        "type(\n" +
                        "  int\n" +
                        ")\n" +
                        "\"test.ml\" 2 10 14 \"test.ml\" 2 10 15\n" +
                        "type(\n" +
                        "  int\n" +
                        ")\n" +
                        "ident(\n" +
                        "  def y \"test.ml\" 2 10 19 \"test.ml\" 0 0 -1\n" +
                        ")\n" +
                        "\"test.ml\" 2 10 18 \"test.ml\" 2 10 19\n" +
                        "type(\n" +
                        "  int\n" +
                        ")",
                "Va|1.4,1.5|x|int\n" +
                "Li|1.8,1.9|int\n" + "Va|2.4,2.5|y|int\n" + "Li|2.8,2.9|int");
    }

    @Test
    public void testLetAndLet() {
        assertParserResult("\"test.ml\" 1 0 4 \"test.ml\" 1 0 5\n" +
                        "type(\n" +
                        "  int\n" +
                        ")\n" +
                        "ident(\n" +
                        "  def x \"test.ml\" 1 0 19 \"test.ml\" 0 0 -1\n" +
                        ")\n" +
                        "\"test.ml\" 1 0 8 \"test.ml\" 1 0 9\n" +
                        "type(\n" +
                        "  int\n" +
                        ")\n" +
                        "\"test.ml\" 1 0 14 \"test.ml\" 1 0 15\n" +
                        "type(\n" +
                        "  int\n" +
                        ")\n" +
                        "ident(\n" +
                        "  def y \"test.ml\" 1 0 19 \"test.ml\" 0 0 -1\n" +
                        ")\n" +
                        "\"test.ml\" 1 0 18 \"test.ml\" 1 0 19\n" +
                        "type(\n" +
                        "  int\n" +
                        ")",
                "Va|1.4,1.5|x|int\n" + "Li|1.8,1.9|int\n" +
                        "Va|1.14,1.15|y|int\n" + "Li|1.18,1.19|int");
    }

    @Test
    public void testLetLetIn() {
        assertParserResult("\"test.ml\" 1 0 4 \"test.ml\" 1 0 5\n" +
                        "type(\n" +
                        "  int\n" +
                        ")\n" +
                        "ident(\n" +
                        "  def x \"test.ml\" 3 22 30 \"test.ml\" 0 0 -1\n" +
                        ")\n" +
                        "\"test.ml\" 2 8 16 \"test.ml\" 2 8 17\n" +
                        "type(\n" +
                        "  int\n" +
                        ")\n" +
                        "ident(\n" +
                        "  def y \"test.ml\" 3 22 29 \"test.ml\" 3 22 30\n" +
                        ")\n" +
                        "\"test.ml\" 2 8 20 \"test.ml\" 2 8 21\n" +
                        "type(\n" +
                        "  int\n" +
                        ")\n" +
                        "\"test.ml\" 3 22 29 \"test.ml\" 3 22 30\n" +
                        "type(\n" +
                        "  int\n" +
                        ")\n" +
                        "ident(\n" +
                        "  int_ref y \"test.ml\" 2 8 16 \"test.ml\" 2 8 17\n" +
                        ")\n" +
                        "\"test.ml\" 2 8 12 \"test.ml\" 3 22 30\n" +
                        "type(\n" +
                        "  int\n" +
                        ")",
                "Va|1.4,1.5|x|int\n" + "Va|2.8,2.9|y|int\n" +
                        "Li|2.12,2.13|int\n" + "Va|3.7,3.8|y|int\n" + "Li|2.4,3.8|int");
    }

    @Test
    public void testLetFunctionAndExternalCall() {
        assertParserResult("\"test.ml\" 1 0 4 \"test.ml\" 1 0 6\n" +
                        "type(\n" +
                        "  'a -> 'a -> 'a\n" +
                        ")\n" +
                        "ident(\n" +
                        "  def mx \"test.ml\" 1 0 20 \"test.ml\" 0 0 -1\n" +
                        ")\n" +
                        "\"test.ml\" 1 0 7 \"test.ml\" 1 0 8\n" +
                        "type(\n" +
                        "  'a\n" +
                        ")\n" +
                        "ident(\n" +
                        "  def a \"test.ml\" 1 0 9 \"test.ml\" 1 0 20\n" +
                        ")\n" +
                        "\"test.ml\" 1 0 9 \"test.ml\" 1 0 10\n" +
                        "type(\n" +
                        "  'a\n" +
                        ")\n" +
                        "ident(\n" +
                        "  def b \"test.ml\" 1 0 13 \"test.ml\" 1 0 20\n" +
                        ")\n" +
                        "\"test.ml\" 1 0 13 \"test.ml\" 1 0 16\n" +
                        "type(\n" +
                        "  'a -> 'a -> 'a\n" +
                        ")\n" +
                        "ident(\n" +
                        "  int_ref Stdlib.max \"stdlib.mli\" 186 7963 7963 \"stdlib.mli\" 186 7963 7987\n" +
                        ")\n" +
                        "\"test.ml\" 1 0 17 \"test.ml\" 1 0 18\n" +
                        "type(\n" +
                        "  'a\n" +
                        ")\n" +
                        "ident(\n" +
                        "  int_ref a \"test.ml\" 1 0 7 \"test.ml\" 1 0 8\n" +
                        ")\n" +
                        "\"test.ml\" 1 0 19 \"test.ml\" 1 0 20\n" +
                        "type(\n" +
                        "  'a\n" +
                        ")\n" +
                        "ident(\n" +
                        "  int_ref b \"test.ml\" 1 0 9 \"test.ml\" 1 0 10\n" +
                        ")\n" +
                        "\"test.ml\" 1 0 13 \"test.ml\" 1 0 20\n" +
                        "type(\n" +
                        "  'a\n" +
                        ")",
                "Va|1.4,1.6|mx|'a -> 'a -> 'a\nVa|1.7,1.8|a|'a\nVa|1.9,1.10|b|'a\n" +
                        "Va|1.13,1.16|Stdlib.max|'a -> 'a -> 'a\n" +
                        "Va|1.17,1.18|a|'a\n" +
                        "Va|1.19,1.20|b|'a\nLi|1.13,1.20|'a");
    }

    @Test
    public void testModuleWithFunctor() {
        assertParserResult("\"test.ml\" 3 15 22 \"test.ml\" 3 15 26\n" +
                        "ident(\n" +
                        "  def Make \"test.ml\" 3 15 45 \"test.ml\" 0 0 -1\n" +
                        ")",
                "Md|3.7,3.11|Make");
    }

    @Test
    public void testModule() {
        assertParserResult("\"test.ml\" 1 0 7 \"test.ml\" 1 0 11\n" +
                        "ident(\n" +
                        "  def Make \"test.ml\" 3 35 38 \"test.ml\" 0 0 -1\n" +
                        ")\n" +
                        "\"test.ml\" 2 21 29 \"test.ml\" 2 21 30\n" +
                        "type(\n" +
                        "  int\n" +
                        ")\n" +
                        "ident(\n" +
                        "  def x \"test.ml\" 2 21 34 \"test.ml\" 3 35 38\n" +
                        ")\n" +
                        "\"test.ml\" 2 21 33 \"test.ml\" 2 21 34\n" +
                        "type(\n" +
                        "  int\n" +
                        ")",
                "Md|1.7,1.11|Make\n" + "Va|2.8,2.9|x|int\n" + "Li|2.12,2.13|int");
    }

    @Test
    public void testUnsavedVariableAndLetFunction() {
        assertParserResult("\"test.ml\" 1 0 4 \"test.ml\" 1 0 5\n" +
                        "type(\n" +
                        "  string\n" +
                        ")\n" +
                        "\"test.ml\" 1 0 8 \"test.ml\" 1 0 23\n" +
                        "type(\n" +
                        "  string\n" +
                        ")\n" +
                        "\"test.ml\" 2 24 32 \"test.ml\" 2 24 33\n" +
                        "type(\n" +
                        "  'a -> 'a\n" +
                        ")\n" +
                        "ident(\n" +
                        "  def f \"test.ml\" 2 24 24 \"test.ml\" 0 0 -1\n" +
                        ")\n" +
                        "\"test.ml\" 2 24 34 \"test.ml\" 2 24 35\n" +
                        "type(\n" +
                        "  'a\n" +
                        ")\n" +
                        "ident(\n" +
                        "  def x \"test.ml\" 2 24 38 \"test.ml\" 2 24 39\n" +
                        ")\n" +
                        "\"test.ml\" 2 24 38 \"test.ml\" 2 24 39\n" +
                        "type(\n" +
                        "  'a\n" +
                        ")\n" +
                        "ident(\n" +
                        "  int_ref x \"test.ml\" 2 24 34 \"test.ml\" 2 24 35\n" +
                        ")",
                "Li|1.4,1.5|string\nLi|1.8,1.23|string\n" +
                        "Va|2.8,2.9|f|'a -> 'a\nVa|2.10,2.11|x|'a\n" +
                        "Va|2.14,2.15|x|'a");
    }

    @Test
    public void testFunctionKeyword() {
        assertParserResult("\"test.ml\" 1 0 4 \"test.ml\" 1 0 5\n" +
                        "type(\n" +
                        "  unit -> unit\n" +
                        ")\n" +
                        "ident(\n" +
                        "  def x \"test.ml\" 2 20 29 \"test.ml\" 0 0 -1\n" +
                        ")\n" +
                        "\"test.ml\" 1 0 17 \"test.ml\" 1 0 19\n" +
                        "type(\n" +
                        "  unit\n" +
                        ")\n" +
                        "\"test.ml\" 2 20 22 \"test.ml\" 2 20 23\n" +
                        "type(\n" +
                        "  unit\n" +
                        ")\n" +
                        "\"test.ml\" 1 0 17 \"test.ml\" 2 20 23\n" +
                        "type(\n" +
                        "  unit\n" +
                        ")\n" +
                        "\"test.ml\" 2 20 27 \"test.ml\" 2 20 29\n" +
                        "type(\n" +
                        "  unit\n" +
                        ")\n" +
                        "\"test.ml\" 1 0 8 \"test.ml\" 2 20 29\n" +
                        "type(\n" +
                        "  unit -> unit\n" +
                        ")",
                "Va|1.4,1.5|x|unit -> unit\nLi|1.17,1.19|unit\n" +
                        "Li|2.2,2.3|unit\nLi|1.17,2.3|unit\nLi|2.7,2.9|unit\n" +
                        "Li|1.8,2.9|unit -> unit");
    }

    @Test
    public void testClass() {
        assertParserResult("\"test.ml\" 2 22 33 \"test.ml\" 2 22 39\n" +
                        "type(\n" +
                        "  < .. >\n" +
                        ")\n" +
                        "\"test.ml\" 3 40 70 \"test.ml\" 3 40 72\n" +
                        "type(\n" +
                        "  int list\n" +
                        ")",
                "Li|2.11,2.17|< .. >\nLi|3.30,3.32|int list");
    }

    @Test
    public void testNewObject() {
        assertParserResult("\"test.ml\" 2 22 33 \"test.ml\" 2 22 39\n" +
                        "type(\n" +
                        "  < .. >\n" +
                        ")\n" +
                        "\"test.ml\" 3 40 70 \"test.ml\" 3 40 72\n" +
                        "type(\n" +
                        "  int list\n" +
                        ")\n" +
                        "\"test.ml\" 5 93 97 \"test.ml\" 5 93 98\n" +
                        "type(\n" +
                        "  stack_of_ints\n" +
                        ")\n" +
                        "ident(\n" +
                        "  def s \"test.ml\" 5 93 118 \"test.ml\" 0 0 -1\n" +
                        ")\n" +
                        "\"test.ml\" 5 93 101 \"test.ml\" 5 93 118\n" +
                        "type(\n" +
                        "  stack_of_ints\n" +
                        ")",
                "Li|2.11,2.17|< .. >\nLi|3.30,3.32|int list\n" +
                        "Va|5.4,5.5|s|stack_of_ints\nLi|5.8,5.25|stack_of_ints");
    }

    @Test
    public void testModuleWithCall() {
        assertParserResult("\"test.ml\" 1 0 44 \"test.ml\" 1 0 51\n" +
                "type(\n" +
                "  'a -> 'a -> int\n" +
                ")\n" +
                "ident(\n" +
                "  def compare \"test.ml\" 1 0 61 \"test.ml\" 1 0 65\n" +
                ")\n" +
                "\"test.ml\" 1 0 54 \"test.ml\" 1 0 61\n" +
                "type(\n" +
                "  'a -> 'a -> int\n" +
                ")\n" +
                "ident(\n" +
                "  int_ref Stdlib.compare \"stdlib.mli\" 93 3855 3855 \"stdlib.mli\" 93 3855 3902\n" +
                ")\n" +
                "\"test.ml\" 1 0 11 \"test.ml\" 1 0 66\n" +
                "call(\n" +
                "  stack\n" +
                ")", "Va|1.44,1.51|compare|'a -> 'a -> int\n" +
                "Va|1.54,1.61|Stdlib.compare|'a -> 'a -> int");
    }

    //
    // bugs
    //

    @Test
    public void test_IssueEmpty() {
        assertParserResult("", null);
    }

    @Test
    public void test_IssueLongValueForType() {
        assertParserResult("\"test.ml\" 58 763 771 \"test.ml\" 58 763 773\n" +
                "type(\n" +
                "  ((int -> int -> int) -> int -> int -> int) ->\n" +
                "  (int -> int -> int) -> int -> int -> int\n" +
                ")\n" +
                "ident(\n" +
                "  int_ref id \"test.ml\" 56 732 736 \"test.ml\" 56 732 738\n" +
                ")", "Va|58.8,58.10|id|((int -> int -> int) -> int -> int -> int) -> (int -> int -> int) -> int -> int -> int");
    }

    @Test // my strategy: the last is the right one :D
    // in fact, they can be different, but last will be the one that will be kept
    public void test_IssueMultipleTypes() {
        assertParserResult("\"err.ml\" 1 0 8 \"err.ml\" 1 0 9\n" +
                "type(\n" +
                "  'a list\n" +
                ")\n" +
                "type(\n" +
                "  'a list\n" +
                ")\n" +
                "ident(\n" +
                "  def l \"err.ml\" 2 20 22 \"err.ml\" 2 20 31\n" +
                ")", "Va|1.8,1.9|l|'a list");
    }

    @Test // keep the same strategy: the last is the right one
    // except that we are NOT accepting ident called *sth*
    public void test_IssueMultipleTypesAndIdent() {
        assertParserResult("\"err.ml\" 1 0 18 \"err.ml\" 1 0 19\n" +
                "type(\n" +
                "  int\n" +
                ")\n" +
                "type(\n" +
                "  int option\n" +
                ")\n" +
                "type(\n" +
                "  int option\n" +
                ")\n" +
                "type(\n" +
                "  int\n" +
                ")\n" +
                "ident(\n" +
                "  int_ref *sth* \"err.ml\" 1 0 18 \"err.ml\" 1 0 19\n" +
                ")\n" +
                "type(\n" +
                "  int\n" +
                ")\n" +
                "type(\n" +
                "  int option\n" +
                ")\n" +
                "type(\n" +
                "  int option\n" +
                ")\n" +
                "ident(\n" +
                "  def *sth* \"err.ml\" 1 0 18 \"err.ml\" 1 0 19\n" +
                ")", "Li|1.18,1.19|int option");
    }

    @Test
    public void test_IssueCall() {
        assertParserResult("\"test_hello_world.ml\" 3 18 26 \"test_hello_world.ml\" 3 18 40\n" +
                        "call(\n" +
                        "  stack\n" +
                        ")\n" +
                        "type(\n" +
                        "  unit\n" +
                        ")",
                "Li|3.8,3.22|unit");
    }

}
