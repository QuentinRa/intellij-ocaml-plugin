package com.ocaml.language.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.ElementBase
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.PlatformIcons
import com.ocaml.icons.OCamlIcons
import com.ocaml.language.OCamlLanguageUtils.pretty
import com.ocaml.language.psi.OCamlLetBinding
import com.ocaml.language.psi.OCamlValueName
import com.ocaml.language.psi.api.OCamlStubbedNamedElementImpl
import com.ocaml.language.psi.api.isAnonymous
import com.ocaml.language.psi.impl.OCamlLetBindingImpl
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
}

fun OCamlLetBinding.computeValueNames(): List<PsiElement> =
    PsiTreeUtil.findChildrenOfType(this, OCamlValueName::class.java).mapNotNull {
        val nameIdentifier = it.lowercaseIdent?.firstChild ?: it
        if ((nameIdentifier as? LeafPsiElement)?.isAnonymous() == true)
            null
        else
            nameIdentifier
    }

fun expandLetBindingStructuredName(structuredName: String?): List<String> {
    if (structuredName.isNullOrEmpty()) return listOf()
    if (!structuredName.contains(",")) return listOf(structuredName)
    val parts = structuredName.split(",").toMutableList()
    val prefix = parts[0].substringBeforeLast('.')
    parts[0] = parts[0].removePrefix("$prefix.")
    return parts.map { part -> "$prefix.$part" }
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

private class OCamlLetBindingDeconstruction(private val psi: PsiElement, private val letBinding: OCamlLetBinding) :
    OCamlLetBindingImpl(letBinding.node) {
    override fun getNameIdentifier(): PsiElement = psi
    override fun getName(): String? {
        // Operators names are formatted by OCaml
        if (psi is OCamlValueName && psi.operatorName != null) return psi.operatorName!!.pretty()
        // Fallback to the default behavior
        return nameIdentifier.text
    }
    override fun isFunction(): Boolean = false

    // Ensure TreeAnchorizer is still working as expected:
    override fun equals(other: Any?): Boolean = letBinding == other
    override fun hashCode(): Int = letBinding.hashCode()
}