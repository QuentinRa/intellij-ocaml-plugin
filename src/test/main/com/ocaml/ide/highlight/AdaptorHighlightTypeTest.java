package com.ocaml.ide.highlight;

import com.intellij.build.FilePosition;
import com.intellij.codeInspection.ProblemHighlightType;
import com.ocaml.OCamlBaseTest;
import com.ocaml.ide.highlight.intentions.OCamlMessageAdaptor;
import com.ocaml.sdk.output.CompilerOutputMessage;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.File;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class AdaptorHighlightTypeTest extends OCamlBaseTest {

    private @NotNull CompilerOutputMessage createCompilerOutputMessage(CompilerOutputMessage.Kind kind) {
        CompilerOutputMessage m = new CompilerOutputMessage();
        m.filePosition = new FilePosition(new File("file.ml"), 0,0);
        m.kind = kind;
        return m;
    }

    // Alert/deprecated

    @Test
    public void testDeprecatedWarning() {
        CompilerOutputMessage m = createCompilerOutputMessage(CompilerOutputMessage.Kind.WARNING);
        m.content = "Warning 3: deprecated: t";
        assertEquals(ProblemHighlightType.LIKE_DEPRECATED, OCamlMessageAdaptor.temper(m).highlightType);
    }

    @Test
    public void testDeprecatedAlert() {
        CompilerOutputMessage m = createCompilerOutputMessage(CompilerOutputMessage.Kind.ALERT);
        m.content = "Alert deprecated: t";
        assertEquals(ProblemHighlightType.LIKE_DEPRECATED, OCamlMessageAdaptor.temper(m).highlightType);
    }

    // Errors/unbound

    @Test
    public void testUnboundConstructor() {
        CompilerOutputMessage m = createCompilerOutputMessage(CompilerOutputMessage.Kind.ERROR);
        m.content = "Error: Unbound constructor A";
        assertEquals(ProblemHighlightType.ERROR, OCamlMessageAdaptor.temper(m).highlightType);
    }

    @Test
    public void testUnboundConstructorType() {
        CompilerOutputMessage m = createCompilerOutputMessage(CompilerOutputMessage.Kind.ERROR);
        m.content = "Error: Unbound type constructor t";
        assertEquals(ProblemHighlightType.ERROR, OCamlMessageAdaptor.temper(m).highlightType);
    }

    @Test
    public void testUnboundModule() {
        CompilerOutputMessage m = createCompilerOutputMessage(CompilerOutputMessage.Kind.ERROR);
        m.content = "Error: Unbound module G";
        assertEquals(ProblemHighlightType.ERROR, OCamlMessageAdaptor.temper(m).highlightType);
    }

    // warnings/missing MLI

    @Test
    public void testMissingMLI() {
        CompilerOutputMessage m = createCompilerOutputMessage(CompilerOutputMessage.Kind.WARNING);
        m.content = "Warning 70: Cannot find interface file.";
        assertTrue(OCamlMessageAdaptor.temper(m).fileLevel);
    }

    // warnings/bad source file name

    @Test
    public void testBadSourceName() {
        CompilerOutputMessage m = createCompilerOutputMessage(CompilerOutputMessage.Kind.WARNING);
        m.content = "Warning 24: bad source file name: \"24_module\" is not a valid module name";
        assertTrue(OCamlMessageAdaptor.temper(m).fileLevel);
    }

    // warnings/unused

    @Test
    public void testUnusedVariable() {
        CompilerOutputMessage m = createCompilerOutputMessage(CompilerOutputMessage.Kind.WARNING);
        m.content = "Warning 27: unused variable v.";
        assertEquals(ProblemHighlightType.LIKE_UNUSED_SYMBOL, OCamlMessageAdaptor.temper(m).highlightType);
    }

    @Test
    public void testUnusedMatchCase() {
        CompilerOutputMessage m = createCompilerOutputMessage(CompilerOutputMessage.Kind.WARNING);
        m.content = "Warning 11: this match case is unused.";
        assertEquals(ProblemHighlightType.LIKE_UNUSED_SYMBOL, OCamlMessageAdaptor.temper(m).highlightType);
    }

    @Test
    public void testUnusedValue() {
        CompilerOutputMessage m = createCompilerOutputMessage(CompilerOutputMessage.Kind.WARNING);
        m.content = "Warning 32: unused value x.";
        assertEquals(ProblemHighlightType.LIKE_UNUSED_SYMBOL, OCamlMessageAdaptor.temper(m).highlightType);
    }

    @Test
    public void testUnusedOpen() {
        CompilerOutputMessage m = createCompilerOutputMessage(CompilerOutputMessage.Kind.WARNING);
        m.content = "Warning 33: unused open T1";
        assertEquals(ProblemHighlightType.LIKE_UNUSED_SYMBOL, OCamlMessageAdaptor.temper(m).highlightType);
    }

    @Test
    public void testUnusedType() {
        CompilerOutputMessage m = createCompilerOutputMessage(CompilerOutputMessage.Kind.WARNING);
        m.content = "Warning 34: unused type t.";
        assertEquals(ProblemHighlightType.LIKE_UNUSED_SYMBOL, OCamlMessageAdaptor.temper(m).highlightType);
    }

    @Test
    public void testUnusedRec() {
        CompilerOutputMessage m = createCompilerOutputMessage(CompilerOutputMessage.Kind.WARNING);
        m.content = "Warning 39: unused rec flag.";
        assertEquals(ProblemHighlightType.LIKE_UNUSED_SYMBOL, OCamlMessageAdaptor.temper(m).highlightType);
    }

    @Test
    public void testUnusedModule() {
        CompilerOutputMessage m = createCompilerOutputMessage(CompilerOutputMessage.Kind.WARNING);
        m.content = "Warning 60: unused module X.";
        assertEquals(ProblemHighlightType.LIKE_UNUSED_SYMBOL, OCamlMessageAdaptor.temper(m).highlightType);
    }

    @Test
    public void testUnusedFunctorParameter() {
        CompilerOutputMessage m = createCompilerOutputMessage(CompilerOutputMessage.Kind.WARNING);
        m.content = "Warning 67: unused functor parameter X.";
        assertEquals(ProblemHighlightType.LIKE_UNUSED_SYMBOL, OCamlMessageAdaptor.temper(m).highlightType);
    }

}
