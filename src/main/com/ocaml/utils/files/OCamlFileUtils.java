package com.ocaml.utils.files;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.ocaml.utils.adaptor.UntilIdeVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

public final class OCamlFileUtils {

    @Nullable
    public static File copyToTempFile(@NotNull File tempCompilationDirectory, @NotNull PsiFile psiFile,
                                      @NotNull String name, Logger logger) {
        File sourceTempFile;

        try {
            sourceTempFile = new File(tempCompilationDirectory, name);
            boolean created = sourceTempFile.createNewFile();
            // FIX avoid "deadlock" if the file already exists
            if (!created && !sourceTempFile.exists())
                throw new IOException("Could not create '" + sourceTempFile + "'.");
        } catch (IOException e) {
            logger.info("Temporary file creation failed", e); // log error but do not show it in UI
            return null;
        }

        try {
            @UntilIdeVersion(release = "203", note = "We can't pass the charset to writeToFile in 203.")
            // use the charset of the original file
            String text = new String(psiFile.getText().getBytes(
                    psiFile.getVirtualFile().getCharset()
            ), psiFile.getVirtualFile().getCharset());
            FileUtil.writeToFile(sourceTempFile, text);
        } catch (IOException e) {
            // Sometimes, file is locked by another process, not a big deal, skip it
            logger.trace("Write failed: " + e.getLocalizedMessage());
            return null;
        }

        return sourceTempFile;
    }

    public static void deleteDirectory(@NotNull String file) {
        deleteDirectory(new File(file));
    }

    public static void deleteDirectory(@NotNull File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    deleteDirectory(subFile);
                }
            }
        }
        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }
}
