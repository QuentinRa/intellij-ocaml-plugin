package com.ocaml;

import com.intellij.lang.Language;
import com.or.lang.utils.ORLanguageProperties;
import org.jetbrains.annotations.NotNull;

public final class OCamlLanguage extends Language implements ORLanguageProperties {
    public static final String FUNCTION_SIGNATURE_SEPARATOR = " -> ";

    @Override
    public @NotNull String getParameterSeparator() {
        return FUNCTION_SIGNATURE_SEPARATOR;
    }

    @Override
    public @NotNull String getFunctionSeparator() {
        return FUNCTION_SIGNATURE_SEPARATOR;
    }

    @Override
    public @NotNull String getTemplateStart() {
        return "";
    }

    @Override
    public @NotNull String getTemplateEnd() {
        return "";
    }
}