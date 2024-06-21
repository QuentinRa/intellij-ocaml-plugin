package com.ocaml.sdk.output.errors;

import com.ocaml.sdk.output.BaseOutputTest;
import com.ocaml.sdk.output.CompilerOutputMessage;
import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class MismatchErrorTest extends BaseOutputTest {

    /*
     * declaration_inverted
     */

    @Test
    public void testDeclarationInverted() {
        String output = "File \"file.ml\", line 1:\n" +
                "Error: The implementation file.ml does not match the interface file.cmi:\n" +
                "       Module type declarations do not match:\n" +
                "         module type Something =\n" +
                "           sig type t val add : int -> int val empty : int end\n" +
                "       does not match\n" +
                "         module type Something =\n" +
                "           sig type t val empty : int val add : int -> int end\n" +
                "       At position module type Something = <here>\n" +
                "       Illegal permutation of structure fields";
        CompilerOutputMessage message = parseError(output);
        assertIsFile(message, "file.ml", 1, 1, -1, -1);
        assertIsContent(message, "Error: The implementation file.ml does not match the interface file.cmi:\n" +
                "       Module type declarations do not match:\n" +
                "         module type Something =\n" +
                "           sig type t val add : int -> int val empty : int end\n" +
                "       does not match\n" +
                "         module type Something =\n" +
                "           sig type t val empty : int val add : int -> int end\n" +
                "       At position module type Something = <here>\n" +
                "       Illegal permutation of structure fields");
    }

    /*
     * missing_impl
     */

    @Test
    public void testMissingImpl() {
        String output = "File \"file.ml\", line 1:\n" +
                "Error: The implementation file.ml does not match the interface file.cmi:\n" +
                "       The module `Make' is required but not provided\n" +
                "       File \"file.mli\", line 7, characters 0-62: Expected declaration\n" +
                "       The module type `Something' is required but not provided\n" +
                "       File \"file.mli\", line 1, characters 0-83: Expected declaration";
        CompilerOutputMessage message = parseError(output);
        assertIsFile(message, "file.ml", 1, 1, -1, -1);
        assertIsContent(message,
                "Error: The implementation file.ml does not match the interface file.cmi:\n" +
                        "       The module `Make' is required but not provided\n" +
                        "       File \"file.mli\", line 7, characters 0-62: Expected declaration\n" +
                        "       The module type `Something' is required but not provided\n" +
                        "       File \"file.mli\", line 1, characters 0-83: Expected declaration");
    }

    /*
     * missing_impl
     */

    @Test
    public void testPartialImpl() {
        String output = "File \"file.ml\", line 7, characters 65-114:\n" +
                "Error: Signature mismatch:\n" +
                "       Modules do not match:\n" +
                "         sig val empty : int val add : 'a -> int end\n" +
                "       is not included in\n" +
                "         sig type t = X.t val empty : int val add : int -> int end\n" +
                "       The type `t' is required but not provided\n" +
                "       File \"file.ml\", line 7, characters 50-62: Expected declaration";
        CompilerOutputMessage message = parseError(output);
        assertIsFile(message, "file.ml", 7, 7, 65, 114);
        assertIsContent(message,
                "Error: Signature mismatch:\n" +
                        "       Modules do not match:\n" +
                        "         sig val empty : int val add : 'a -> int end\n" +
                        "       is not included in\n" +
                        "         sig type t = X.t val empty : int val add : int -> int end\n" +
                        "       The type `t' is required but not provided\n" +
                        "       File \"file.ml\", line 7, characters 50-62: Expected declaration");
    }

}
