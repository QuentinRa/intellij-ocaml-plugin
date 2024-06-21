package com.ocaml.ide.insight.parameters;

import com.intellij.lang.parameterInfo.CreateParameterInfoContext;
import com.intellij.lang.parameterInfo.ParameterInfoHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A fake context used by OCamlParameterInfo. Store the "result" in
 * "toShow".
 */
public class FakeCreateParameterInfoContext implements CreateParameterInfoContext {

    private final Project project;
    private final PsiElement caret;
    public Object[] toShow;

    public FakeCreateParameterInfoContext(Project project, PsiElement caret) {
        this.project = project;
        this.caret = caret;
    }

    @Override public Project getProject() {
        return project;
    }

    @Override public PsiFile getFile() {
        return caret.getContainingFile();
    }

    @Override public int getOffset() {
        return caret.getTextOffset() + 1;
    }

    @Override public void setItemsToShow(Object[] items) {
        toShow = items;
    }

    @Override public Object @Nullable [] getItemsToShow() {
        throw new UnsupportedOperationException();
    }

    @Override public void showHint(PsiElement element, int offset, ParameterInfoHandler handler) {
        throw new UnsupportedOperationException();
    }

    @Override public int getParameterListStart() {
        throw new UnsupportedOperationException();
    }

    @Override public PsiElement getHighlightedElement() {
        throw new UnsupportedOperationException();
    }

    @Override public void setHighlightedElement(PsiElement elements) {
        throw new UnsupportedOperationException();
    }

    @Override public @NotNull Editor getEditor() {
        throw new UnsupportedOperationException();
    }
}
