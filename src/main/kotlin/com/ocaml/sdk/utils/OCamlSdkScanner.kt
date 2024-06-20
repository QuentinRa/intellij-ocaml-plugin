package com.ocaml.sdk.utils

import java.nio.file.Path

object OCamlSdkScanner {
    fun scanAll(files: Collection<Path>, includeNestDirs: Boolean): Set<String> {
        val result: MutableSet<String> = HashSet()
        for (root in HashSet<Path>(files)) {
            scanFolder(root, includeNestDirs, result)
        }
        return result
    }

    private fun scanFolder(folder: Path, includeNestDirs: Boolean, result: MutableCollection<in String>) {
        // Check if the folder is valid
        if (OCamlSdkHomeUtils.isValid(folder)) {
            result.add(folder.toAbsolutePath().toString())
            return
        }

        // explore
        if (!includeNestDirs) return
        val files = folder.toFile().listFiles() ?: return

        for (candidate in files) {
            for (adjusted in listOf(candidate)) {
                scanFolder(adjusted.toPath(), false, result)
            }
        }
    }
}