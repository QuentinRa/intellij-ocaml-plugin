package com.dune.sdk.runConfiguration

import com.dune.DuneBundle
import com.dune.language.parser.DuneKeywords
import com.dune.language.psi.DuneArgument
import com.dune.language.psi.DuneAtom
import com.dune.language.psi.DuneList
import com.dune.language.psi.DuneTypes
import com.intellij.execution.Executor
import com.intellij.execution.Location
import com.intellij.execution.PsiLocation
import com.intellij.execution.RunManagerEx
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.execution.runners.ExecutionUtil
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.psi.PsiElement

private val ACTION_ICON = AllIcons.RunConfigurations.TestState.Run

class DuneTargetRunLineMarkerContributor : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? {
        // Extract "xxx" from dune file to generate the run configuration
        // Dune file: "(executable (name xxx))"
        val elementType = element.node.elementType
        val base : PsiElement = when (elementType) {
            // <leaf> - namedAtom - atom
            DuneTypes.ATOM_VALUE_ID -> element.parent?.parent as? DuneAtom
            DuneTypes.STRING_VALUE -> element
            else -> null
        } ?: return null
        //  argument - list   <--->    (name ...)
        val name = (base.parent as? DuneArgument)?.parent as? DuneList ?: return null
        if (name.value?.text != DuneKeywords.NAME) return null
        // argument - list    <--->   (executable ...)
        val root = name.parent?.parent as? DuneList ?: return null
        if (root.value?.text != DuneKeywords.EXECUTABLE) return null

        return Info(ACTION_ICON, { "" }, DuneRunTargetAction(element))
    }
}

// Copyright 2000-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
@Suppress("DialogTitleCapitalization")
class DuneRunTargetAction(private val target: PsiElement) :
    AnAction(DuneBundle.message("action.run.target.text", target.text.replace("_", "__")),
        DuneBundle.message("action.run.target.description", target.text),
        ACTION_ICON) {
    override fun actionPerformed(event: AnActionEvent) {
        val dataContext = SimpleDataContext.getSimpleContext(Location.DATA_KEY, PsiLocation(target), event.dataContext)
        val context = ConfigurationContext.getFromContext(dataContext, event.place)
        val producer = DuneRunConfigurationProducer()
        val configuration = producer.findOrCreateConfigurationFromContext(context)?.configurationSettings ?: return
        (context.runManager as RunManagerEx).setTemporaryConfiguration(configuration)
        ExecutionUtil.runConfiguration(configuration, Executor.EXECUTOR_EXTENSION_NAME.extensionList.first())
    }

}