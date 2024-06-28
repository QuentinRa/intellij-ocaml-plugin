package com.ocaml.language.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.ElementBase
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.util.PsiUtil
import com.intellij.util.PlatformIcons
import com.ocaml.icons.OCamlIcons
import com.ocaml.language.psi.OCamlTypedef
import com.ocaml.language.psi.api.OCamlStubbedNamedElementImpl
import com.ocaml.language.psi.api.isAnonymous
import com.ocaml.language.psi.stubs.impl.OCamlTypeDefStub
import javax.swing.Icon

abstract class OCamlTypeDefMixin : OCamlStubbedNamedElementImpl<OCamlTypeDefStub>, OCamlTypedef {
    constructor(node: ASTNode) : super(node)
    constructor(stub: OCamlTypeDefStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameIdentifier(): PsiElement? {
        val name = typeconstrName.lowercaseIdent
        return if ((name as? LeafPsiElement)?.isAnonymous() == true)
            null
        else
            name
    }

    override fun getIcon(flags: Int): Icon? {
        val visibilityIcon = PlatformIcons.PUBLIC_ICON
        val icon = OCamlIcons.Nodes.TYPE
        return ElementBase.iconWithVisibilityIfNeeded(flags, icon, visibilityIcon)
    }

    override fun getAccessLevel(): Int = PsiUtil.ACCESS_LEVEL_PUBLIC
    override fun getSubLevel(): Int = 0
}