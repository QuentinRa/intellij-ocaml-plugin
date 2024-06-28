package com.ocaml.language.psi.api

import com.intellij.psi.impl.source.tree.LeafPsiElement

// fixme: rename to OCamlVariableDeclaration
interface OCamlLetDeclaration {
    fun isFunction() : Boolean
    fun isVariable() : Boolean

    /**
     * Is this variable or function directly inside the file (global=true)
     * or it is inside a module/class/another let statement/etc. (global=false)?
     */
    fun isGlobal() : Boolean
}

fun OCamlNamedElement.isAnonymous() : Boolean = name == null || name == "_"
fun LeafPsiElement.isAnonymous() : Boolean = text == "_"