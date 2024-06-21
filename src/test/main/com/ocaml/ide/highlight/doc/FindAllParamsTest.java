package com.ocaml.ide.highlight.doc;

import com.ocaml.OCamlBaseTest;
import com.ocaml.ide.highlight.OCamlDocumentationAnnotator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is testing that comments are properly highlighted
 * with the annotator. The test is checking that the pattern is valid,
 * but the highlight is quite basic, for now.
 */
@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class FindAllParamsTest extends OCamlBaseTest {

    @Test
    public void testArgModuleComment() {
        String text = "(** [Arg.parse speclist anon_fun usage_msg] parses the command line.\n" +
                "    [speclist] is a list of triples [(key, spec, doc)].\n" +
                "    [key] is the option keyword, it must start with a ['-'] character.\n" +
                "    [spec] gives the option type and the function to call when this option\n" +
                "    is found on the command line.\n" +
                "    [doc] is a one-line description of this option.\n" +
                "    [anon_fun] is called on anonymous arguments.\n" +
                "    The functions in [spec] and [anon_fun] are called in the same order\n" +
                "    as their arguments appear on the command line.\n" +
                "\n" +
                "    If an error occurs, [Arg.parse] exits the program, after printing\n" +
                "    to standard error an error message as follows:\n" +
                "-   The reason for the error: unknown option, invalid or missing argument, etc.\n" +
                "-   [usage_msg]\n" +
                "-   The list of options, each followed by the corresponding [doc] string.\n" +
                "    Beware: options that have an empty [doc] string will not be included in the\n" +
                "    list.\n" +
                "\n" +
                "    For the user to be able to specify anonymous arguments starting with a\n" +
                "    [-], include for example [(\"-\", String anon_fun, doc)] in [speclist].\n" +
                "\n" +
                "    By default, [parse] recognizes two unit options, [-help] and [--help],\n" +
                "    which will print to standard output [usage_msg] and the list of\n" +
                "    options, and exit the program.  You can override this behaviour\n" +
                "    by specifying your own [-help] and [--help] options in [speclist].\n" +
                "*)";
        Pattern pattern = OCamlDocumentationAnnotator.PARAMETERS_MATCHER;
        Matcher matcher = pattern.matcher(text);
        ArrayList<String> matches = new ArrayList<>();
        // iterate results
        while (matcher.find()) {
            // add if OK
            String match = matcher.group();
            matches.add(match);
        }
        assertEquals(24, matches.size());
    }

}
