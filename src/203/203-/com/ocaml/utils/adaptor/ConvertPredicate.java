package com.ocaml.utils.adaptor;

import java.util.function.Predicate;

public class ConvertPredicate {

    // it's funny right? the trick is that this method got a different type in 203
    public static Predicate<Object[]> cast(Predicate<Object[]> condition) {
        return condition;
    }

}
