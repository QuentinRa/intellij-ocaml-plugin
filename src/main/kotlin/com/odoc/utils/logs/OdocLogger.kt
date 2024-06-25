package com.odoc.utils.logs

import com.intellij.openapi.diagnostic.Logger

/**
 * Create a logger for the plugin<br></br>
 * See https://plugins.jetbrains.com/docs/intellij/ide-infrastructure.html#error-reporting
 */
object OdocLogger {
    val instance: Logger
        get() = Logger.getInstance("odoc")
}
