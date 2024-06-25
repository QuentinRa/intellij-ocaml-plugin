package com.odoc.utils.logs;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Create a logger for the plugin<br>
 * See https://plugins.jetbrains.com/docs/intellij/ide-infrastructure.html#error-reporting
 */
public class OdocLogger {

    public static @NotNull Logger getInstance() {
        return Logger.getInstance("odoc");
    }

}
