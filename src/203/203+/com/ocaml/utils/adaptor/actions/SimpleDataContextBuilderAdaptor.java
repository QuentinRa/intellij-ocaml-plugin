package com.ocaml.utils.adaptor.actions;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SimpleDataContextBuilderAdaptor {

    @Contract(value = " -> new", pure = true)
    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private DataContext myParent;
        private Map<String, Object> myMap;

        public Builder setParent(@Nullable DataContext parent) {
            myParent = parent;
            return this;
        }

        public <T> Builder add(DataKey<T> dataKey, T value) {
            if (value != null) {
                if (myMap == null) myMap = new HashMap<>();
                myMap.put(dataKey.getName(), value);
            }
            return this;
        }

        @NotNull
        public DataContext build() {
            if (myMap == null && myParent == null) return DataContext.EMPTY_CONTEXT;
            return SimpleDataContext.getSimpleContext(myMap != null ? myMap : Collections.emptyMap(), myParent);
        }
    }
}
