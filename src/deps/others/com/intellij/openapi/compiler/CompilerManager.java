package com.intellij.openapi.compiler;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * THIS FILE IS NOT USED IN PRODUCTION.
 * That's only because I need it to compile the project in minor IDEs, because the real
 * one require the Java plugin (in production, the real one is not loaded, so there is
 * no problem).
 */
public class CompilerManager {
    private final Project project;

    public CompilerManager(Project project) {
        this.project = project;
    }

    public static CompilerManager getInstance(Project project) {
        return new CompilerManager(project);
    }

    public boolean isExcludedFromCompilation(VirtualFile virtualFile) {
        return ProjectFileIndex.getInstance(project).isInContent(virtualFile);
    }
}
