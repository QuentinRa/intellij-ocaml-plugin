package com.ocaml.lang.utils;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import com.ocaml.OCamlBaseTest;
import org.intellij.lang.annotations.Language;
import org.junit.Test;

import java.util.Iterator;
import java.util.Set;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class OCamlResolveDependenciesTest extends OCamlBaseTest {

    @Override protected String getCustomTestDataPath() {
        return "com.lang.utils/deps/";
    }

    @Test
    public void testFindDependencies() {
        final String FILE_NAME = "deps.ml";
        @Language("OCaml") String originalMlCode = loadFile(FILE_NAME);
        assertNotNull(originalMlCode);
        PsiFile file = myFixture.configureByText(FILE_NAME, originalMlCode);
        Set<String> dependencies = OCamlResolveDependencies.findDependencies(file);
        assertSize(5, dependencies);
        assertContainsElements(dependencies, "toto", "set", "tata", "hello_world", "stdlib");
    }

    /**
     * Note that test_hello_world is opening Hello_world and Set (native).
     * hello_world.mli is opening Toto.
     * Toto isn't opening anything new, we aren't considering the .mli (opening Tata).
     */
    @Test
    public void testResolveDependencies() {
        // create files
        myFixture.configureByText("hello_world.ml", loadFileNonNull("hello_world.ml"));
        myFixture.configureByText("hello_world.mli", loadFileNonNull("hello_world.mli"));
        myFixture.configureByText("tata.ml", loadFileNonNull("tata.ml"));
        myFixture.configureByText("toto.ml", loadFileNonNull("toto.ml"));
        myFixture.configureByText("toto.mli", loadFileNonNull("toto.mli"));
        PsiFile sourceFile = myFixture.configureByText("test_hello_world.ml", loadFileNonNull("test_hello_world.ml"));

        // not inside a module?
        Module module = ModuleUtil.findModuleForFile(sourceFile.getVirtualFile(), myFixture.getProject());
        assertNotNull(module);
        // find root manager
        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);

        Set<Pair<String, PsiFile>> pairs = OCamlResolveDependencies.resolveForFile(sourceFile, moduleRootManager);
        assertSize(2, pairs);
        Iterator<Pair<String, PsiFile>> iterator = pairs.iterator();
        Pair<String, PsiFile> next = iterator.next();
        assertEquals("toto.mli", next.first);
        assertEquals("toto.mli", next.second.getName());
        next = iterator.next();
        assertEquals("hello_world.mli", next.first);
        assertEquals("hello_world.mli", next.second.getName());
    }

}
