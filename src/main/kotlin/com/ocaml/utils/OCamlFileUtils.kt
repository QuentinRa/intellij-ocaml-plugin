package com.ocaml.utils

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VfsUtil
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*

object OCamlFileUtils {
    /**
     * Load a file stored in resources.
     * The path must starts with a /.
     * The separators for the lines in the returned file will be \n.
     */
    fun loadFileContent(filePath: String, logger: Logger? = null): String {
        try {
            val url =
                OCamlFileUtils::class.java.classLoader.getResource(filePath.replaceFirst("/".toRegex(), ""))
                    ?: throw IOException("Couldn't get URL for $filePath")
            val virtualFile = VfsUtil.findFileByURL(url) ?: throw IOException("Couldn't find file by URL for $filePath")
            val text = VfsUtil.loadText(virtualFile)
            val split = text.split("\r\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return java.lang.String.join("\n", *split)
        } catch (e: IOException) {
            logger?.error("Error loading file $filePath", e)
            return ""
        }
    }

    /**
     * Create a file with some text.
     */
    fun createFile(sourceRootFile: File, fileName: String, text: String, logger: Logger? = null) {
        try {
            val mainFile = File(sourceRootFile, fileName)
            Files.write(
                mainFile.toPath(),
                ArrayList(listOf(*text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))
            )
        } catch (e: IOException) {
            logger?.error("Error creating file $fileName", e)
        }
    }
}