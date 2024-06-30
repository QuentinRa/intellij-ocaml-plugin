package com.dune.sdk.runConfiguration

import com.dune.DuneBundle
import com.dune.language.parser.DuneKeywords
import com.dune.language.parser.DuneTargetExtension
import com.dune.language.psi.DuneArgument
import com.dune.language.psi.DuneAtom
import com.dune.language.psi.DuneList
import com.dune.language.psi.DuneTypes
import com.dune.sdk.api.DuneCommand
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
import javax.swing.Icon

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
        if (name.value?.text != DuneKeywords.NAME && name.value?.text != DuneKeywords.NAMES) return null
        // argument - list    <--->   (executable ...)
        val root = name.parent?.parent as? DuneList ?: return null
        val (extension, command) = when(root.value?.text) {
            DuneKeywords.EXECUTABLE, DuneKeywords.EXECUTABLES -> DuneTargetExtension.EXECUTABLE to DuneCommand.EXEC
            DuneKeywords.LIBRARY -> DuneTargetExtension.LIBRARY to DuneCommand.BUILD
            else -> return null
        }
        val icon = when (extension) {
            DuneTargetExtension.LIBRARY -> AllIcons.Actions.Compile
            DuneTargetExtension.EXECUTABLE -> AllIcons.RunConfigurations.TestState.Run
            else -> error("Extension: $extension is not supported yet.")
        }
        return Info(icon, { "" }, DuneRunTargetAction(element, command, extension.value, icon))
    }
}

// Copyright 2000-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
@Suppress("DialogTitleCapitalization")
class DuneRunTargetAction(private val target: PsiElement, private val command: DuneCommand, private val extension: String, icon: Icon) :
    AnAction(
        DuneBundle.message("action.run.target.text", command.value, target.text.replace("_", "__")),
        DuneBundle.message("action.run.target.description", command.value, target.text),
        icon) {
    override fun actionPerformed(event: AnActionEvent) {
        val dataContext = SimpleDataContext.getSimpleContext(Location.DATA_KEY, PsiLocation(target), event.dataContext)
        val context = ConfigurationContext.getFromContext(dataContext, event.place)
        val producer = DuneRunConfigurationProducer(command, extension)
        val configuration = producer.findOrCreateConfigurationFromContext(context)?.configurationSettings ?: return
        (context.runManager as RunManagerEx).setTemporaryConfiguration(configuration)
        ExecutionUtil.runConfiguration(configuration, Executor.EXECUTOR_EXTENSION_NAME.extensionList.first())
    }

}