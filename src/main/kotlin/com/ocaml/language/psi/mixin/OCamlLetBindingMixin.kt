package com.ocaml.language.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.ElementBase
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.util.PsiUtil
import com.intellij.util.PlatformIcons
import com.ocaml.icons.OCamlIcons
import com.ocaml.language.OCamlLanguageUtils.pretty
import com.ocaml.language.psi.OCamlLetBinding
import com.ocaml.language.psi.OCamlValueName
import com.ocaml.language.psi.api.OCamlStubbedNamedElementImpl
import com.ocaml.language.psi.api.isAnonymous
import com.ocaml.language.psi.mixin.utils.computeValueNames
import com.ocaml.language.psi.stubs.impl.OCamlLetBindingStub
import javax.swing.Icon

abstract class OCamlLetBindingMixin : OCamlStubbedNamedElementImpl<OCamlLetBindingStub>, OCamlLetBinding {
    constructor(node: ASTNode) : super(node)
    constructor(stub: OCamlLetBindingStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameIdentifier(): PsiElement? {
        val name = getNameIdentifierWithAnonymous()
        return if ((name as? LeafPsiElement)?.isAnonymous() == true)
            null
        else
            name
    }

    internal fun getNameIdentifierWithAnonymous() : PsiElement? {
        return valueName?.lowercaseIdent?.firstChild ?: valueName
    }

    override fun getName(): String? {
        // Operators names are formatted by OCaml
        if (valueName?.operatorName != null) return valueName!!.operatorName!!.pretty()
        // Handle pattern variables
        if (valueName == null) return computeValueNames().joinToString(",") {
            // Operators names are formatted by OCaml
            if (it is OCamlValueName && it.operatorName != null) it.operatorName!!.pretty()
            else it.text
        }
        // Fallback to the default behavior
        return super.getName()
    }

    override fun isFunction(): Boolean {
        // we should check the type as this check is not enough
        // but there is no type inference yet
        return getParameterList().isNotEmpty()
    }

    // PsiFile.LetBindings.<this>
    override fun isGlobal(): Boolean = parent.parent is PsiFile

    override fun getIcon(flags: Int): Icon? {
        val visibilityIcon = PlatformIcons.PUBLIC_ICON
        val icon = if (isFunction()) OCamlIcons.Nodes.FUNCTION else OCamlIcons.Nodes.LET
        return ElementBase.iconWithVisibilityIfNeeded(flags, icon, visibilityIcon)
    }

    override fun getAccessLevel(): Int = PsiUtil.ACCESS_LEVEL_PUBLIC
    override fun getSubLevel(): Int = 0
}