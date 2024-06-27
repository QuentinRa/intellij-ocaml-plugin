package com.ocaml.language.psi.api

import com.intellij.openapi.util.UserDataHolderEx
import com.intellij.psi.*

// an element
interface OCamlElement : PsiElement, UserDataHolderEx

// an element with a name (ex: var_name)
interface OCamlNamedElement : OCamlElement, PsiNamedElement, NavigatablePsiElement

// an element with a qualified name (ex: Mod.classname.var_name)
interface OCamlQualifiedNamedElement : OCamlNamedElement, PsiQualifiedNamedElement

// an element with a name given from another element
interface OCamlNameIdentifierOwner : OCamlQualifiedNamedElement, PsiNameIdentifierOwner