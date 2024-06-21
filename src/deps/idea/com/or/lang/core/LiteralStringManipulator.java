package com.or.lang.core;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiExpression;
import com.intellij.util.IncorrectOperationException;
import com.or.lang.core.psi.PsiLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// com.intellij.psi.impl.source.resolve.reference.impl.manipulators.StringLiteralManipulator
public class LiteralStringManipulator extends AbstractElementManipulator<PsiLiteralExpression> {
    @NotNull
    public static TextRange getValueRange(@NotNull PsiLiteralExpression expression) {
        return new TextRange(1, Math.max(1, expression.getTextLength() - 1));
    }

    @Override
    public @Nullable PsiLiteralExpression handleContentChange(@NotNull PsiLiteralExpression element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        String oldText = element.getText();
        if (oldText.startsWith("\"")) {
            newContent = StringUtil.escapeStringCharacters(newContent);
        } else if (oldText.startsWith("'") && newContent.length() <= 1) {
            newContent = newContent.length() == 1 && newContent.charAt(0) == '\'' ? "\\'" : newContent;
        } else {
            throw new IncorrectOperationException("cannot handle content change for: " + oldText + ", expr: " + element);
        }

        String newText = oldText.substring(0, range.getStartOffset()) + newContent + oldText.substring(range.getEndOffset());
        final PsiExpression newExpr = JavaPsiFacade.getElementFactory(element.getProject()).createExpressionFromText(newText, null);
        return (PsiLiteralExpression) element.replace(newExpr);
    }

    @Override
    public @NotNull TextRange getRangeInElement(@NotNull PsiLiteralExpression element) {
        return getValueRange(element);
//    return getStringTokenRange(element);
    }
}
