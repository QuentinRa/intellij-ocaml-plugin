package com.ocaml.sdk.output.errors;

import com.ocaml.sdk.output.BaseOutputTest;
import com.ocaml.sdk.output.CompilerOutputMessage;
import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class UnboundErrorTest extends BaseOutputTest {

    /*
     * constructor.ml
     */

    @Test
    public void testUnboundConstructor() {
        String output = "File \"constructor.ml\", line 1, characters 8-9:\n" +
                "1 | let x = A\n" +
                "            ^\n" +
                "Error: Unbound constructor A";
        CompilerOutputMessage message = parseError(output);
        assertIsFile(message, "constructor.ml", 1, 1, 8, 9);
        assertIsContent(message, "Error: Unbound constructor A");
        assertIsContext(message,  "1 | let x = A\n" + "            ^\n");
    }

    /*
     * constructor_type.ml
     */

    @Test
    public void testUnboundConstructorType() {
        String output = "File \"constructor_type.ml\", line 1, characters 8-9:\n" +
                "1 | let x : t = 0\n" +
                "            ^\n" +
                "Error: Unbound type constructor t";
        CompilerOutputMessage message = parseError(output);
        assertIsFile(message, "constructor_type.ml", 1, 1, 8, 9);
        assertIsContent(message, "Error: Unbound type constructor t");
        assertIsContext(message,  "1 | let x : t = 0\n" + "            ^\n");
    }

    /*
     * open.ml
     */

    @Test
    public void testUnboundModule() {
        String output = "File \"open.ml\", line 1, characters 5-6:\n" +
                "1 | open G\n" +
                "         ^\n" +
                "Error: Unbound module G";
        CompilerOutputMessage message = parseError(output);
        assertIsFile(message, "open.ml", 1, 1, 5, 6);
        assertIsContent(message, "Error: Unbound module G");
        assertIsContext(message,  "1 | open G\n" + "         ^\n");
    }

    /*
     * rec.ml
     */

    @Test
    public void testMissingRec() {
        String output = "File \"rec.ml\", line 1, characters 10-11:\n" +
                "1 | let x y = x y;;\n" +
                "              ^\n" +
                "Error: Unbound value x\n" +
                "Hint: If this is a recursive definition,\n" +
                "you should add the 'rec' keyword on line 1";
        CompilerOutputMessage message = parseError(output);
        assertIsFile(message, "rec.ml", 1, 1, 10, 11);
        assertIsContent(message, "Error: Unbound value x\n" +
                "Hint: If this is a recursive definition,\n" +
                "you should add the 'rec' keyword on line 1");
        assertIsContext(message,  "1 | let x y = x y;;\n" + "              ^\n");
    }

}
