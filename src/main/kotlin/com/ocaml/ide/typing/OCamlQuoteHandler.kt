package com.ocaml.ide.typing

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import com.ocaml.language.psi.OCamlTypes

class OCamlQuoteHandler : SimpleTokenSetQuoteHandler(OCamlTypes.STRING_VALUE)