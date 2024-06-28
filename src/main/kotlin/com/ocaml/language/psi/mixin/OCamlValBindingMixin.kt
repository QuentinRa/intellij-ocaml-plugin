package com.ocaml.language.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.ElementBase
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.util.PsiUtil
import com.intellij.util.PlatformIcons
import com.ocaml.icons.OCamlIcons
import com.ocaml.language.OCamlLanguageUtils.pretty
import com.ocaml.language.psi.OCamlImplUtils
import com.ocaml.language.psi.OCamlTypes
import com.ocaml.language.psi.OCamlValueBinding
import com.ocaml.language.psi.api.OCamlStubbedNamedElementImpl
import com.ocaml.language.psi.stubs.impl.OCamlValBindingStub
import javax.swing.Icon

abstract class OCamlValBindingMixin : OCamlStubbedNamedElementImpl<OCamlValBindingStub>, OCamlValueBinding {
    constructor(node: ASTNode) : super(node)
    constructor(stub: OCamlValBindingStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameIdentifier(): PsiElement? = valueName.firstChild?.firstChild ?: valueName

    override fun getName(): String? {
        // Operators names are formatted by OCaml
        if (valueName.operatorName != null) return valueName.operatorName!!.pretty()
        // Fallback to the default behavior
        return super.getName()
    }

    override fun isFunction() : Boolean {
        // we should actually check the type using type inference
        return OCamlImplUtils.nextSiblingWithTokenType(typexpr.firstChild, OCamlTypes.RIGHT_ARROW) != null
    }

    // PsiFile.ValueDescription.<this>
    override fun isGlobal(): Boolean = parent?.parent is PsiFile

    override fun getIcon(flags: Int): Icon? {
        val visibilityIcon = PlatformIcons.PUBLIC_ICON
        val icon = if (isFunction()) OCamlIcons.Nodes.FUNCTION else OCamlIcons.Nodes.LET
        return ElementBase.iconWithVisibilityIfNeeded(flags, icon, visibilityIcon)
    }

    override fun getAccessLevel(): Int = PsiUtil.ACCESS_LEVEL_PUBLIC
    override fun getSubLevel(): Int = 0
}