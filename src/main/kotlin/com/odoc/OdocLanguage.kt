package com.odoc

import com.intellij.lang.Language
import com.ocaml.language.OCamlLanguage

object OdocLanguage : Language("odoc") {
    private fun readResolve(): Any = OCamlLanguage
}
