package com.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.impl.PsiElementBase;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * THIS FILE IS NOT USED IN PRODUCTION.
 * That's only because I need it to compile the project in minor IDEs, because the real
 * one require the Java plugin (in production, the real one is not loaded, so there is
 * no problem).
 */
public class PsiClass extends PsiElementBase implements PsiNamedElement {
    @Override public @NotNull Language getLanguage() {
        return Language.ANY;
    }

    @Override public @NotNull PsiElement[] getChildren() {
        return new PsiElement[0];
    }

    @Override public PsiElement getParent() {
        return null;
    }

    @Override public TextRange getTextRange() {
        return null;
    }

    @Override public int getStartOffsetInParent() {
        return 0;
    }

    @Override public int getTextLength() {
        return 0;
    }

    @Override public @Nullable PsiElement findElementAt(int offset) {
        return null;
    }

    @Override public int getTextOffset() {
        return 0;
    }

    @Override public String getText() {
        return null;
    }

    @Override public @NotNull char[] textToCharArray() {
        return new char[0];
    }

    @Override public ASTNode getNode() {
        return null;
    }

    @Override public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
}
