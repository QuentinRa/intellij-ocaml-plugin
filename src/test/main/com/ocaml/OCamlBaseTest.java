package com.ocaml;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.ocaml.ide.insight.OCamlAnnotResultsService;
import com.ocaml.utils.adaptor.UntilIdeVersion;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class OCamlBaseTest extends BasePlatformTestCase {

    @UntilIdeVersion(release = "203")
    @SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase") @Test @Ignore
    public void testFake() {}

    public static String OCAML_CARET = "(*caret*)";
    public static String OCAML_SELECT_START = "(*selection*)";
    public static String OCAML_SELECT_END = "(*/selection*)";
    public static String EXPECTED_CARET = "<caret>";
    public static String EXPECTED_SELECT_START = "<selection>";
    public static String EXPECTED_SELECT_END = "</selection>";

    protected @NotNull PsiElement configureCodeWithCaret(@Language("OCaml") @NotNull String code) {
        String cleanedCode = code.replace(OCAML_CARET, EXPECTED_CARET);
        cleanedCode = cleanedCode.replace(OCAML_SELECT_START, EXPECTED_SELECT_START);
        cleanedCode = cleanedCode.replace(OCAML_SELECT_END, EXPECTED_SELECT_END);
        // create file
        PsiFile file = myFixture.configureByText("file.ml", cleanedCode);

        int caretOffset = myFixture.getCaretOffset();
        assertNotSame(-1, caretOffset);

        PsiElement elementAt = file.findElementAt(caretOffset-1);
        assertNotNull(elementAt);
        return elementAt;
    }

    protected @NotNull PsiElement configureCodeWithCaret(String filename, @NotNull String text) {
        @Language("OCaml") String code = loadFile(filename);
        assertNotNull(code);

        PsiFile file = myFixture.configureByText(filename, code);
        int i = file.getText().indexOf(text.replace("(*caret*)", ""));
        assertTrue("Caret missing in '"+text+"'", text.contains("(*caret*)"));
        assertTrue("Matching code not found for '"+text+"'", i != -1);
        i += text.indexOf("(*caret*)")-1;
        PsiElement caret = file.findElementAt(i);
        assertNotNull(caret);
        return caret;
    }

    protected @Nullable String loadFile(@NotNull String fileName) {
        try {
            return FileUtil.loadFile(new File(getTestDataPath(), fileName), CharsetToolkit.UTF8, true);
        } catch (IOException e) {
            return null;
        }
    }

    protected void configureAnnotResultsService(@NotNull PsiElement caret, String filename) {
        OCamlAnnotResultsService annot = getProject().getService(OCamlAnnotResultsService.class);
        File annotations = new File(getTestDataPath(), filename);
        annot.updateForFile(caret.getContainingFile().getVirtualFile().getPath(), annotations);
    }

    protected @NotNull String loadFileNonNull(String fileName) {
        String s = loadFile(fileName);
        assertNotNull(s);
        return s;
    }

    protected String getCustomTestDataPath() {
        return "";
    }

    @Override protected final @NotNull String getTestDataPath() {
        return "test/testData/"+getCustomTestDataPath();
    }

}
