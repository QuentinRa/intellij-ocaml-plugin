package com.ocaml.language.psi.mixin.fake

import com.intellij.psi.PsiElement
import com.ocaml.language.OCamlLanguageUtils.pretty
import com.ocaml.language.psi.OCamlLetBinding
import com.ocaml.language.psi.OCamlValueName
import com.ocaml.language.psi.api.OCamlFakeElement
import com.ocaml.language.psi.impl.OCamlLetBindingImpl

class OCamlLetBindingDeconstruction(private val psi: PsiElement, override val source: OCamlLetBinding) :
    OCamlLetBindingImpl(source.node), OCamlFakeElement {

    override fun getNameIdentifier(): PsiElement = psi
    override fun getName(): String? {
        // Operators names are formatted by OCaml
        if (psi is OCamlValueName && psi.operatorName != null) return psi.operatorName!!.pretty()
        // Fallback to the default behavior
        return nameIdentifier.text
    }
    override fun isFunction(): Boolean = false

    // Ensure TreeAnchorizer is still working as expected:
    override fun equals(other: Any?): Boolean = source == other
    override fun hashCode(): Int = source.hashCode()
}