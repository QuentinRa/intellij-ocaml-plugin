package com.ocaml.utils.adaptor;

import com.intellij.openapi.Disposable;
import com.intellij.util.Alarm;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class AlarmAdaptor {

    @Contract("_, _ -> new") @UntilIdeVersion(release = "211")
    // it means this class is a copy of the one in 2O3
    public static @NotNull Alarm createAlarm(JComponent component, Disposable disposable) {
        return new Alarm(Alarm.ThreadToUse.SWING_THREAD, disposable)
                .setActivationComponent(component);
    }
}
