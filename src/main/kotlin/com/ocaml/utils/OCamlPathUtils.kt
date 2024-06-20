package com.ocaml.utils

import com.intellij.openapi.diagnostic.Logger
import java.nio.file.Files
import java.nio.file.Path

object OCamlPathUtils {
    /**
     * Create a symbolicLink
     *
     * @param src    the link that will be created
     * @param dest   the destination of the link
     * @param logger log an error that occurred when creating a symbolic link
     * @param args   parts of the path leading to the destination
     */
    fun createSymbolicLink(src: String, dest: String, logger: Logger?, vararg args: String?): Boolean {
        try {
            val link = Path.of(dest, *args)
            val target = Path.of(src)
            Files.createSymbolicLink(link, target)
            return true
        } catch (e: Exception) {
            logger?.error("Could not create link from '$src' to '$dest'.", e)
            return false
        }
    }
}
