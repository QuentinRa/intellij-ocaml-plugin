package com.ocaml.ide.files;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.ocaml.OCamlLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FileHelper {

    public static boolean isOCamlContext(@NotNull DataContext context) {
        var editor = context.getData(CommonDataKeys.EDITOR);
        if (editor == null) return false;
        var document = editor.getDocument();
        var project = context.getData(CommonDataKeys.PROJECT);
        if (project == null) return false;
        var psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (psiFile == null) return false;
        var language = psiFile.getLanguage();
        return language == OCamlLanguage.INSTANCE;
    }

    public static boolean isInterface(@Nullable FileType fileType) {
        return fileType instanceof OCamlInterfaceFileType;
    }
}
