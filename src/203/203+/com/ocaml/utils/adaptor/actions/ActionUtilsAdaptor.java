package com.ocaml.utils.adaptor.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import org.jetbrains.annotations.NotNull;

public class ActionUtilsAdaptor {

    public static void performActionDumbAwareWithCallbacks(@NotNull AnAction action, @NotNull AnActionEvent e) {
        ActionUtil.performActionDumbAwareWithCallbacks(action, e, e.getDataContext());
    }

}
