package com.ocaml.utils.adaptor;

import com.intellij.openapi.util.Condition;

public class ConvertPredicate {

    public static Condition<Object[]> cast(Condition<Object[]> condition) {
        return condition;
    }

}
