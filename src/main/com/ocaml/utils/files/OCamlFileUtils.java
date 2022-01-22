package com.ocaml.utils.files;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

public final class OCamlFileUtils {

    /**
     * Load a file stored in resources.
     * The path must starts with a /.
     * The separators for the lines in the returned file will be \n.
     */
    public static @NotNull String loadFileContent(@NotNull String filePath, @Nullable Logger logger) {
        try {
            var url = OCamlFileUtils.class.getClassLoader().getResource(filePath.replaceFirst("/", ""));
            if (url == null) throw new IOException("Couldn't get URL for " + filePath);
            VirtualFile virtualFile = VfsUtil.findFileByURL(url);
            if (virtualFile == null) throw new IOException("Couldn't find file by URL for " + filePath);
            String text = VfsUtil.loadText(virtualFile);
            String[] split = text.split("\r\n");
            return String.join("\n", split);
        } catch (IOException e) {
            if (logger != null) logger.error("Error loading file " + filePath, e);
            return "";
        }
    }

    /**
     * Create a file with some text.
     */
    public static void createFile(@NotNull File sourceRootFile, @NotNull String fileName,
                                  @NotNull String text, @Nullable Logger logger) {
        try {
            File mainFile = new File(sourceRootFile, fileName);
            Files.write(mainFile.toPath(), new ArrayList<>(Arrays.asList(text.split("\n"))));
        } catch (IOException e) {
            if (logger != null) logger.error("Error creating file " + fileName, e);
        }
    }

    public static void deleteDirectory(String file) {
        deleteDirectory(new File(file));
    }

    public static void deleteDirectory(File file) {
        if(file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile: files) {
                    deleteDirectory(subFile);
                }
            }
        }
        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }
}
