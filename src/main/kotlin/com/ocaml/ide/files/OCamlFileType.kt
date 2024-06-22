package com.ocaml.ide.files

import com.intellij.openapi.fileTypes.LanguageFileType
import com.ocaml.OCamlBundle
import com.ocaml.icons.OCamlIcons
import com.ocaml.language.OCamlLanguage
import org.jetbrains.annotations.Contract
import javax.swing.Icon

object OCamlFileType : LanguageFileType(OCamlLanguage) {
    // Extension-Related Constants
    internal const val DEFAULT_EXTENSION = "ml"
    internal const val DOT_DEFAULT_EXTENSION = ".$DEFAULT_EXTENSION"

    // LanguageFileType implementation
    override fun getName(): String  = "OCaml File"
    override fun getDescription(): String = OCamlBundle.message("filetype.ml.description")
    override fun getDefaultExtension(): String = DEFAULT_EXTENSION
    override fun getIcon(): Icon = OCamlIcons.FileTypes.OCAML_SOURCE
    override fun getDisplayName(): String  = description

    /**
     * Given a path to an interface, return the path to the source
     *
     * @param path path to the interface file, must be properly formatted
     * @return the path to the source
     * @throws IllegalArgumentException if the path is valid using [OCamlFileType.isFile]
     */
    fun fromInterface(path: String): String {
        if (OCamlInterfaceFileType.isFile(path)) return path.replace(
            OCamlInterfaceFileType.DEFAULT_EXTENSION,
            DEFAULT_EXTENSION
        )
        throw IllegalArgumentException("Not a valid interface file '$path'")
    }
    @Contract(pure = true)
    fun isFile(name: String): Boolean {
        return name.endsWith(DOT_DEFAULT_EXTENSION)
    }
}