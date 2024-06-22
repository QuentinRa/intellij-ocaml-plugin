package com.ocaml.ide.files.utils

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import com.ocaml.ide.files.OCamlFileType
import com.ocaml.ide.files.OCamlInterfaceFileType

object OCamlFileUtils {
    private fun isOCaml(fileType: FileType?): Boolean {
        return fileType is OCamlFileType || fileType is OCamlInterfaceFileType
    }

    fun isOCaml(file: VirtualFile): Boolean {
        return isOCaml(file.fileType)
    }
}