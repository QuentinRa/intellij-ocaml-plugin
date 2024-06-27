package com.odoc.language

import com.intellij.lang.Language

object OdocLanguage : Language("odoc") {
    private fun readResolve(): Any = OdocLanguage
}
