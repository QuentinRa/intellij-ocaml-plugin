package com.ocaml.utils.strings;

import com.ocaml.OCamlBaseTest;
import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class StringsUtilTest extends OCamlBaseTest {

    @Test
    public void testCapitalize() {
        assertEquals("Toto", StringsUtil.capitalize("toto"));
    }

    @Test
    public void testCapitalizeCapitalized() {
        assertEquals("Toto", StringsUtil.capitalize("Toto"));
    }
}
