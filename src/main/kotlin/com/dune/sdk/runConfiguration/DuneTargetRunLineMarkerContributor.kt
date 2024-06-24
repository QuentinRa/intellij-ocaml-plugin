@file:Suppress("DialogTitleCapitalization")

package com.dune.sdk.runConfiguration

import com.dune.DuneBundle
import com.dune.language.parser.DuneKeywords
import com.dune.language.psi.DuneArgument
import com.dune.language.psi.DuneTypes
import com.dune.language.psi.DuneValue
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

private val ACTION_ICON = AllIcons.RunConfigurations.TestState.Run

class DuneTargetRunLineMarkerContributor : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? {
        // "NAME"/"NAMES" is a VALUE | "xxx"/"yyy" are arguments
        // (name xxx)
        // (names xxx yyy)
        if (element.node.elementType === DuneTypes.VALUE) {
            val nameElement = element as DuneValue
            val targetList : List<PsiElement> = when (nameElement.atom?.namedAtom?.name) {
                DuneKeywords.NAME, DuneKeywords.NAMES -> {
                    val x = PsiTreeUtil.getChildrenOfTypeAsList(element.parent, DuneArgument::class.java).mapNotNull {
                        it.stringValue ?: it.atom?.namedAtom
                    }
                    x
                }
                else -> null
            } ?: return null
            if (targetList.isEmpty()) return null
            return Info(ACTION_ICON, { "" }, *targetList.map(::DuneRunTargetAction).toTypedArray())
        }
        return null
    }
}

class DuneRunTargetAction(private val target: PsiElement) :
    AnAction(DuneBundle.message("action.run.target.text", target.text.replace("_", "__")),
        DuneBundle.message("action.run.target.description", target.text),
        ACTION_ICON) {
    override fun actionPerformed(e: AnActionEvent) {
        TODO("Not yet implemented")
    }

}