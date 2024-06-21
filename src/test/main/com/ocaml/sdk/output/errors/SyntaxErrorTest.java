package com.ocaml.sdk.output.errors;

import com.ocaml.sdk.output.BaseOutputTest;
import com.ocaml.sdk.output.CompilerOutputMessage;
import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class SyntaxErrorTest extends BaseOutputTest {

    /*
     * in.ml
     */

    @Test
    public void testUnboundConstructor() {
        String output = "File \"in.ml\", line 2, characters 8-10:\n" +
                "2 |         in y\n" +
                "            ^^\n" +
                "Error: Syntax error";
        CompilerOutputMessage message = parseError(output);
        assertIsFile(message, "in.ml", 2, 2, 8, 10);
        assertIsContent(message, "Error: Syntax error");
        assertIsContext(message,  "2 |         in y\n" + "            ^^\n");
    }

    /*
     * let.ml
     */

    @Test
    public void testUnboundConstructorType() {
        String output = "File \"let.ml\", line 1, characters 3-3:\n" +
                "Error: Syntax error";
        CompilerOutputMessage message = parseError(output);
        assertIsFile(message, "let.ml", 1, 1, 3, 3);
        assertIsContent(message, "Error: Syntax error");
    }

}
