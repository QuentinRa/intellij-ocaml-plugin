package com.ocaml.ide.files

import com.intellij.openapi.fileTypes.LanguageFileType
import com.ocaml.OCamlBundle
import com.ocaml.icons.OCamlIcons
import com.ocaml.language.OCamlInterfaceLanguage
import org.jetbrains.annotations.Contract
import javax.swing.Icon

object OCamlInterfaceFileType : LanguageFileType(OCamlInterfaceLanguage) {
    // Extension-Related Constants
    internal const val DEFAULT_EXTENSION = "mli"
    internal const val DOT_DEFAULT_EXTENSION = ".$DEFAULT_EXTENSION"

    // LanguageFileType implementation
    override fun getName(): String  = "OCaml Interface File"
    override fun getDescription(): String = OCamlBundle.message("filetype.mli.description")
    override fun getDefaultExtension(): String = DEFAULT_EXTENSION
    override fun getIcon(): Icon = OCamlIcons.FileTypes.OCAML_INTERFACE
    override fun getDisplayName(): String  = description

    /**
     * Given a path to a source file, return the path to the interface
     *
     * @param path path to the source file, must be properly formatted
     * @return the path to the interface
     * @throws IllegalArgumentException if the path is valid using [OCamlFileType.isFile]
     */
    fun fromSource(path: String): String {
        if (OCamlFileType.isFile(path)) return path.replace(OCamlFileType.DEFAULT_EXTENSION, DEFAULT_EXTENSION)
        throw IllegalArgumentException("Not a valid source file '$path'")
    }

    @Contract(pure = true)
    fun isFile(name: String): Boolean {
        return name.endsWith(DOT_DEFAULT_EXTENSION)
    }
}