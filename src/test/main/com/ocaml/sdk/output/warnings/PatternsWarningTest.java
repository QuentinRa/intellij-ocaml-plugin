package com.ocaml.sdk.output.warnings;

import com.ocaml.sdk.output.BaseOutputTest;
import com.ocaml.sdk.output.CompilerOutputMessage;
import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class PatternsWarningTest extends BaseOutputTest {

    /*
     * pattern-matching is not exhaustive
     */

    @Test
    public void testPatternMatchingNotExhaustiveContext() {
        String output = "File \"patterns.ml\", line 3, characters 14-36:\n" +
                "3 | ..............match y with\n" +
                "4 | | A -> 0\n" +
                "Warning 8: this pattern-matching is not exhaustive.\n" +
                "Here is an example of a case that is not matched:\n" +
                "(B|C)";
        CompilerOutputMessage message = parseWarning(output);
        assertIsFile(message, "patterns.ml", 3, 3, 14, 36);
        assertIsContent(message, "Warning 8: this pattern-matching is not exhaustive.\n" +
                "Here is an example of a case that is not matched:\n" +
                "(B|C)");
        assertIsContext(message,  "3 | ..............match y with\n" + "4 | | A -> 0\n");
    }

    /*
     * pattern-matching is fragile
     */

    @Test
    public void testFragileMatchPatternContextMnemonic() {
        String output = "File \"patterns.ml\", lines 10-12, characters 15-8:\n" +
                "10 | ...............match y with\n" +
                "11 | | A -> 0\n" +
                "12 | | _ -> 0\n" +
                "Warning 4 [fragile-match]: this pattern-matching is fragile.\n" +
                "It will remain exhaustive when constructors are added to type t2.";
        CompilerOutputMessage message = parseWarning(output);
        assertIsFile(message, "patterns.ml", 10, 12, 15, 8);
        assertIsContent(message, "Warning 4: this pattern-matching is fragile.\n" +
                "It will remain exhaustive when constructors are added to type t2.");
        assertIsContext(message,   "10 | ...............match y with\n" +
                "11 | | A -> 0\n" + "12 | | _ -> 0\n");
    }
}
