package com.ocaml.language.psi.api

import com.intellij.ide.structureView.impl.java.AccessLevelProvider
import com.intellij.openapi.util.UserDataHolderEx
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.CompositePsiElement
import com.intellij.psi.tree.IElementType

// an element
interface OCamlElement : PsiElement, UserDataHolderEx

// an element with a name (ex: var_name)
interface OCamlNamedElement : OCamlElement, PsiNamedElement, NavigatablePsiElement

// an element with a qualified name (ex: Mod.classname.var_name)
interface OCamlQualifiedNamedElement : OCamlNamedElement, PsiQualifiedNamedElement

// an element with a name given from another element
interface OCamlNameIdentifierOwner : OCamlQualifiedNamedElement, PsiNameIdentifierOwner, AccessLevelProvider

// a fake element created from a source
interface OCamlFakeElement : OCamlElement {
    val source : PsiElement
}

// Base class for elements implementation
abstract class OCamlElementImpl(type: IElementType) : CompositePsiElement(type)