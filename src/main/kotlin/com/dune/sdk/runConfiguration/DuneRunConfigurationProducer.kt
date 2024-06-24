// Copyright 2000-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.dune.sdk.runConfiguration

import com.dune.language.psi.files.DuneFile
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.components.PathMacroManager
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import java.io.File

class DuneRunConfigurationProducer : LazyRunConfigurationProducer<DuneRunConfiguration>() {
    override fun setupConfigurationFromContext(configuration: DuneRunConfiguration, context: ConfigurationContext, sourceElement: Ref<PsiElement>): Boolean {
        if (context.psiLocation?.containingFile !is DuneFile) return false
        val macroManager = PathMacroManager.getInstance(context.project)
        val path = context.location?.virtualFile?.path
        configuration.filename = macroManager.collapsePath(path) ?: ""
        configuration.target = findTarget(context)?.text ?: "" // fixme: ...

        if (configuration.target.isNotEmpty()) {
            configuration.name = configuration.target
        } else {
            configuration.name = File(path).name
        }

        return true
    }

    override fun isConfigurationFromContext(configuration: DuneRunConfiguration, context: ConfigurationContext): Boolean {
        val macroManager = PathMacroManager.getInstance(context.project)
        return macroManager.expandPath(configuration.filename) == context.location?.virtualFile?.path &&
                configuration.target == findTarget(context)?.text // fixme: ...
    }

    private fun findTarget(context: ConfigurationContext): PsiElement? {
        var element = context.psiLocation
        // fixme: ...
        return element
    }

    override fun getConfigurationFactory(): ConfigurationFactory = DuneRunConfigurationFactory(
        DuneRunConfigurationType.instance)
}