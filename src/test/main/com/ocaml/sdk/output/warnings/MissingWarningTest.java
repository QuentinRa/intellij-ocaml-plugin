package com.ocaml.sdk.output.warnings;

import com.ocaml.sdk.SinceOCamlVersion;
import com.ocaml.sdk.output.BaseOutputTest;
import com.ocaml.sdk.output.CompilerOutputMessage;
import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class MissingWarningTest extends BaseOutputTest {

    @Test @SinceOCamlVersion(since = "4.13.0")
    public void testMissingMLI() {
        String output = "File \"missing.ml\", line 1:\n" +
                "Warning 70 [missing-mli]: Cannot find interface file.";
        CompilerOutputMessage message = parseWarning(output);
        assertIsFile(message, "missing.ml", 1, 1, -1, -1);
        assertIsContent(message,"Warning 70: Cannot find interface file.");
    }

}
