package com.ocaml.sdk.providers;

import com.ocaml.OCamlBaseTest;
import com.ocaml.sdk.providers.cygwin.CygwinFolders;
import com.ocaml.sdk.providers.linux.LinuxFolders;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class BaseFolderProviderTest extends OCamlBaseTest {

    @Test
    public void testAtLeastOnceProviderAvailable() {
        boolean isBinAvailable = false;
        boolean isOpamAvailable = false;

        // Cygwin64
        BaseFolderProvider cygwin64 = new CygwinFolders(CygwinFolders.CYGWIN_FOLDER);
        if (cygwin64.isBinAvailable()) isBinAvailable = true;
        if (cygwin64.isOpamAvailable()) isOpamAvailable = true;
        logFolderProvide(cygwin64);

        // OCaml64
        BaseFolderProvider ocaml64 = new CygwinFolders(CygwinFolders.OCAML64_FOLDER);
        if (ocaml64.isBinAvailable()) isBinAvailable = true;
        if (ocaml64.isOpamAvailable()) isOpamAvailable = true;
        logFolderProvide(ocaml64);

        // Linux
        BaseFolderProvider linux = new LinuxFolders();
        if (linux.isBinAvailable()) isBinAvailable = true;
        if (linux.isOpamAvailable()) isOpamAvailable = true;
        logFolderProvide(linux);

        // WSL is available since 203+
        try {
            Class<?> aClass = Class.forName("com.ocaml.sdk.providers.wsl.WSLFolders");
            BaseFolderProvider instance = (BaseFolderProvider) aClass.getDeclaredConstructor().newInstance();

            if (instance.isBinAvailable()) isBinAvailable = true;
            if (instance.isOpamAvailable()) isOpamAvailable = true;

            logFolderProvide(instance);
        } catch (ClassNotFoundException | NoSuchMethodException |
                InstantiationException | IllegalAccessException | InvocationTargetException ignore) {}

        assertTrue(isBinAvailable || isOpamAvailable);
    }

    private void logFolderProvide(@NotNull BaseFolderProvider instance) {
        String name = instance.getName();
        System.out.println("  + "+name);
        System.out.println("    - bin : "+instance.isBinAvailable());
        System.out.println("    - opam : "+instance.isOpamAvailable());
    }

}
