package com.ocaml.language.psi.mixin.utils

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.ocaml.language.psi.OCamlLetBinding
import com.ocaml.language.psi.OCamlLetBindings
import com.ocaml.language.psi.OCamlValueName
import com.ocaml.language.psi.api.isAnonymous
import com.ocaml.language.psi.mixin.OCamlLetBindingMixin
import com.ocaml.language.psi.mixin.fake.OCamlLetBindingDeconstruction

fun OCamlLetBinding.computeValueNames(): List<PsiElement> {
    val results = mutableListOf<OCamlValueName>()
    // only these elements can have names
    exprList.forEach { results.addAll(PsiTreeUtil.findChildrenOfType(it, OCamlValueName::class.java)) }
    patternNoExn?.let { results.addAll(PsiTreeUtil.findChildrenOfType(it, OCamlValueName::class.java)) }
    // map them
    return results.mapNotNull {
        val nameIdentifier = it.lowercaseIdent?.firstChild ?: it
        if ((nameIdentifier as? LeafPsiElement)?.isAnonymous() == true)
            null
        else
            nameIdentifier
    }
}

fun OCamlLetBinding.getNestedLetBindings(): List<OCamlLetBindings> {
    return letBindingBody?.exprList?.flatMap { it.children.toList() }?.mapNotNull {
        when (it) {
            is OCamlLetBindings -> it
            else -> it.firstChild as? OCamlLetBindings // EXPR.<OCamlLetBindings>
        }
    } ?: emptyList()
}

fun expandLetBindingStructuredName(structuredName: String?, fqn: Boolean): List<String> {
    if (structuredName.isNullOrEmpty()) return listOf()
    if (!structuredName.contains(",")) return listOf(structuredName)
    val parts = structuredName.split(",").toMutableList()
    if (fqn) {
        val prefix = parts[0].substringBeforeLast('.')
        parts[0] = parts[0].removePrefix("$prefix.")
        return parts.map { part -> "$prefix.$part" }
    } else {
        return parts
    }
}

fun handleStructuredLetBinding(letBinding: OCamlLetBinding): List<PsiElement> {
    if ((letBinding as OCamlLetBindingMixin).getNameIdentifierWithAnonymous() == null) {
        // we are expanding children variable names
        return letBinding.computeValueNames().map {
            OCamlLetBindingDeconstruction(it, letBinding)
        }
    } else if (letBinding.isAnonymous()) {
        return listOf()
    }
    return listOf(letBinding)
}