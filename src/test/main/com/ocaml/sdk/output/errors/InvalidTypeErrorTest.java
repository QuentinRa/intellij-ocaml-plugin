package com.ocaml.sdk.output.errors;

import com.ocaml.sdk.output.BaseOutputTest;
import com.ocaml.sdk.output.CompilerOutputMessage;
import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class InvalidTypeErrorTest extends BaseOutputTest {

    /*
     * expr_type.ml
     */

    @Test
    public void testInvalidExpressionType() {
        String output = "File \"expr_type.ml\", line 2, characters 8-9:\n" +
                "2 | let y = x 0\n" +
                "            ^\n" +
                "Error: This expression has type float\n" +
                "       This is not a function; it cannot be applied.";
        CompilerOutputMessage message = parseError(output);
        assertIsFile(message, "expr_type.ml", 2, 2, 8, 9);
        assertIsContent(message,
                "Error: This expression has type float\n" +
                        "       This is not a function; it cannot be applied.");
        assertIsContext(message,  "2 | let y = x 0\n" + "            ^\n");
    }

}
