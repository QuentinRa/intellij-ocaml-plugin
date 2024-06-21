package com.ocaml.lang.utils;

import com.ocaml.OCamlBaseTest;
import com.ocaml.OCamlLanguage;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class OCamlConsumeParametersTest extends OCamlBaseTest {

    /*
    List<String> parameters = elements.stream().map(pair -> pair.first.type).collect(Collectors.toList());
            // handle the types that we got before us
//        for (String arg: parameters) {
//            if (arg.contains(separator))
//                arg = "("+arg+")";
//            arg += separator;
//            // stop, we can't continue
//            if (!function.contains(arg)) break;
//            // this is a regex, we need to escape (
//            arg = arg.replace("(", "\\(").replace(")", "\\)");
//            // remove
//            function = function.replaceFirst(arg, "");
//        }
     */
    public void consume(String function, String ... params) {
        final String separator = OCamlLanguage.FUNCTION_SIGNATURE_SEPARATOR;

        // we iterated the context and found a list of parameters
        List<String> parameters = Arrays.asList(params);

        for (String arg: parameters) {
            if (arg.contains(separator))
                arg = "("+arg+")";
            arg += separator;
            System.out.println("fun:"+function);
            System.out.println(" arg:"+arg);
            // stop, we can't continue
            if (!function.contains(arg)) break;
            // this is a regex, we need to escape (
            arg = arg.replace("(", "\\(").replace(")", "\\)");
            // remove
            function = function.replaceFirst(arg, "");
            System.out.println(" new:"+function);
        }

        while (function.contains(separator)) {
            int i = function.indexOf(separator);

            // oh, no, this is a function
            String substring = function.substring(0, i);
            if (substring.startsWith("(")) {
                i = function.indexOf(')') + 1; // the separator is after ')'
                substring = function.substring(1, i-1); // we don't want '(' nor ')'
            }

            // handle
            System.out.println("look for:"+ substring);

            // next
            function = function.substring(i + separator.length());
        }
    }

    @Test
    public void testAfterAll() {
        consume("(int -> int -> int) -> int -> int -> int",
                "int -> int -> int", "int", "int");
    }

    @Test
    public void testAfterTwo() {
        consume("(int -> int -> int) -> int -> int -> int",
                "int -> int -> int", "int");
    }

    @Test
    public void testAfterOne() {
        consume("(int -> int -> int) -> int -> int -> int",
                "int -> int -> int");
    }

    @Test
    public void testFunOnly() {
        consume("(int -> int -> int) -> int -> int -> int");
    }

//    @Test
//    public void testOneParamOk() {
//        consume("unit -> unit", "unit");
//        // all parameters were submitted
//    }
//
//    @Test
//    public void testTwoParamsOk() {
//        consume("int -> float -> unit", "int", "float");
//        // all parameters were submitted
//    }
//
//    @Test
//    public void testTypesIParamsOk() {
//        consume("int -> float -> unit", "int", "float");
//        // all parameters were submitted
//    }
}
