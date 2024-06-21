package com.ocaml.utils.adaptor;

import com.intellij.openapi.Disposable;
import com.intellij.util.Alarm;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class AlarmAdaptor {

    @Contract("_, _ -> new") @SinceIdeVersion(release = "212")
    public static @NotNull Alarm createAlarm(JComponent component, Disposable disposable) {
        return new Alarm(component, disposable);
    }
}
