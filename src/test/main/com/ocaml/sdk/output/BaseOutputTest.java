package com.ocaml.sdk.output;

import com.ocaml.OCamlBaseTest;
import com.ocaml.utils.adaptor.UntilIdeVersion;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Utilities to test the parser of the compiler
 * output
 */
public class BaseOutputTest extends OCamlBaseTest {

    @UntilIdeVersion(release = "203")
    @SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase") @Test @Ignore
    public void testFake() {}

    protected CompilerOutputMessage parseWarning(String output) {
        return parse(output, CompilerOutputMessage.Kind.WARNING);
    }

    protected CompilerOutputMessage parseError(String output) {
        return parse(output, CompilerOutputMessage.Kind.ERROR);
    }

    protected CompilerOutputMessage parse(String output, CompilerOutputMessage.Kind kind) {
        ArrayList<CompilerOutputMessage> messages = parseMessages(output);
        assertSize(1, messages);

        CompilerOutputMessage message = messages.get(0);
        assertEquals(kind, message.kind);
        return message;
    }

    protected ArrayList<CompilerOutputMessage> parseMessages(@NotNull String text) {
        ArrayList<CompilerOutputMessage> messages = new ArrayList<>();
        CompilerOutputParser compilerOutputParser = new CompilerOutputParser() {
            @Override protected void onMessageReady(@NotNull CompilerOutputMessage message) {
                messages.add(message);
            }
        };
        text.lines().forEach(compilerOutputParser::parseLine);
        compilerOutputParser.inputDone();
        return messages;
    }

    protected void assertIsFile(@NotNull CompilerOutputMessage message, String filePath,
                                int startLine, int endLine, int startCol, int endCol) {
        assertNotNull(message.filePosition);
        assertNotNull(message.filePosition.getFile());
        assertTrue(message.filePosition.getFile().getAbsolutePath().contains(filePath));
        assertEquals(message.filePosition.getStartLine(), startLine);
        assertEquals(message.filePosition.getEndLine(), endLine);
        assertEquals(message.filePosition.getStartColumn(), startCol);
        assertEquals(message.filePosition.getEndColumn(), endCol);
    }

    protected void assertIsContext(@NotNull CompilerOutputMessage message, String expected) {
        assertEquals(expected, message.context);
    }

    protected void assertIsContent(@NotNull CompilerOutputMessage message, String expected) {
        assertEquals(expected+"\n", message.content);
    }

}
