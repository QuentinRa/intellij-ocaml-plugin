package com.ocaml.sdk.output.warnings;

import com.ocaml.sdk.output.BaseOutputTest;
import com.ocaml.sdk.output.CompilerOutputMessage;
import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public final class PartialWarningTest extends BaseOutputTest {

    /*
     * partial function application
     */

    @Test
    public void testPartialFunctionApplicationContextMnemonic() {
        String output = "File \"partial.ml\", line 2, characters 14-17:\n" +
                "2 | let y : int = x 0\n" +
                "                  ^^^\n" +
                "Warning 5 [ignored-partial-application]: this function application is partial,\n" +
                "maybe some arguments are missing.";
        CompilerOutputMessage message = parseWarning(output);
        assertIsFile(message, "partial.ml", 2, 2, 14, 17);
        assertIsContent(message,"Warning 5: this function application is partial,\n" +
                "maybe some arguments are missing.");
        assertIsContext(message,  "2 | let y : int = x 0\n" + "                  ^^^\n");
    }

}