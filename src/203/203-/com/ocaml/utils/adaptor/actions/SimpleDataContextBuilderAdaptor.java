package com.ocaml.utils.adaptor.actions;

import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class SimpleDataContextBuilderAdaptor {

    @Contract(value = " -> new", pure = true)
    public static @NotNull SimpleDataContext.Builder builder() {
        return SimpleDataContext.builder();
    }
}
