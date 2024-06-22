// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.ocaml.sdk.runConfiguration

import com.intellij.diagnostic.logging.LogConfigurationPanel
import com.intellij.execution.*
import com.intellij.execution.application.JvmMainMethodRunConfigurationOptions
import com.intellij.execution.configurations.*
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.target.LanguageRuntimeType
import com.intellij.execution.target.TargetEnvironmentAwareRunProfile
import com.intellij.execution.target.TargetEnvironmentConfiguration
import com.intellij.execution.target.java.JavaLanguageRuntimeConfiguration
import com.intellij.execution.target.java.JavaLanguageRuntimeType
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.options.SettingsEditorGroup
import com.intellij.openapi.util.WriteExternalException
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.search.ExecutionSearchScopes
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.PathUtil
import org.jdom.Element

// JavaRunConfigurationBase
open class OCamlRunConfiguration(name: String?, runConfigurationModule: JavaRunConfigurationModule, factory: ConfigurationFactory?) :
    ModuleBasedConfiguration<JavaRunConfigurationModule, Element>(name, runConfigurationModule, factory!!),
    CommonJavaRunConfigurationParameters, TargetEnvironmentAwareRunProfile {

    init {
        runConfigurationModule.setModuleToAnyFirstIfNotSpecified()
    }

    override fun getValidModules(): Collection<Module> = ModuleManager.getInstance(project).modules.toList()
    override fun getSearchScope(): GlobalSearchScope? = ExecutionSearchScopes.executionScope(modules.toList())

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration?> {
        val group = SettingsEditorGroup<OCamlRunConfiguration>()
        group.addEditor(ExecutionBundle.message("run.configuration.configuration.tab.title"),
            OCamlRunConfigurationEditor(project))
        JavaRunConfigurationExtensionManager.instance.appendEditors(this, group)
        group.addEditor(ExecutionBundle.message("logs.tab.title"), LogConfigurationPanel())
        return group
    }

    override fun getOptions() = super.getOptions() as JvmMainMethodRunConfigurationOptions

    override fun readExternal(element: Element) {
        super.readExternal(element)
        JavaRunConfigurationExtensionManager.instance.readExternal(this, element)
    }

    @Throws(WriteExternalException::class)
    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        JavaRunConfigurationExtensionManager.instance.writeExternal(this, element)
    }

    protected open fun defaultWorkingDirectory() = project.basePath
    override fun setWorkingDirectory(value: String?) {
        val normalizedValue = if (StringUtil.isEmptyOrSpaces(value)) null else value!!.trim { it <= ' ' }
        val independentValue = PathUtil.toSystemIndependentName(normalizedValue)
        options.workingDirectory = independentValue?.takeIf { it != defaultWorkingDirectory() }
    }
    override fun getWorkingDirectory(): String? =
        options.workingDirectory?.let {
            FileUtilRt.toSystemDependentName(VirtualFileManager.extractPath(it))
        } ?: PathUtil.toSystemDependentName(defaultWorkingDirectory())


    override fun getVMParameters(): String? = options.vmParameters
    override fun setVMParameters(value: String?) {
        options.vmParameters = value
    }

    override fun getProgramParameters(): String? = options.programParameters
    override fun setProgramParameters(value: String?) {
        options.programParameters = value
    }

    override fun isPassParentEnvs(): Boolean = options.isPassParentEnv
    override fun setPassParentEnvs(passParentEnvs: Boolean) {
        options.isPassParentEnv = passParentEnvs
    }

    override fun getEnvs(): Map<String, String> = options.env
    override fun setEnvs(envs: Map<String, String>) {
        options.env = envs.toMutableMap()
    }

    override fun getRunClass(): String? = options.mainClassName
    fun setRunClass(value: String?) {
        options.mainClassName = value
    }

    override fun getPackage(): String? = null

    override fun isAlternativeJrePathEnabled(): Boolean = options.isAlternativeJrePathEnabled
    override fun setAlternativeJrePathEnabled(enabled: Boolean) {
        options.isAlternativeJrePathEnabled = enabled
    }
    override fun getAlternativeJrePath(): String? = options.alternativeJrePath
    override fun setAlternativeJrePath(path: String?) {
        options.alternativeJrePath = path
    }

    @Throws(RuntimeConfigurationException::class)
    override fun checkConfiguration() {
        // fixme: no checks for now
    }

    @Throws(ExecutionException::class)
    override fun getState(executor: Executor, executionEnvironment: ExecutionEnvironment): RunProfileState? {
        return OCamlRunConfigurationCommandLineState(this, executionEnvironment)
    }

    override fun suggestedName(): String? {
        val runClass = runClass
        if (StringUtil.isEmpty(runClass)) return null
        val parts = StringUtil.split(runClass!!, ".")
        return if (parts.isEmpty()) {
            runClass
        } else parts[parts.size - 1]
    }

    override fun canRunOn(target: TargetEnvironmentConfiguration): Boolean =
        target.runtimes.findByType(JavaLanguageRuntimeConfiguration::class.java) != null

    override fun getDefaultLanguageRuntimeType(): LanguageRuntimeType<*>? =
        LanguageRuntimeType.EXTENSION_NAME.findExtension(JavaLanguageRuntimeType::class.java)

    override fun getDefaultTargetName(): String? = options.remoteTarget
    override fun setDefaultTargetName(targetName: String?) {
        options.remoteTarget = targetName
    }

    // fixme: ............
    protected fun runsUnderWslJdk() = true

    override fun needPrepareTarget() = super.needPrepareTarget() || runsUnderWslJdk()

    // todo: refer to JavaCommandLineState
    private class OCamlRunConfigurationCommandLineState(private val configuration: OCamlRunConfiguration,
                                                        environment: ExecutionEnvironment?) : CommandLineState(environment) {

        override fun startProcess(): ProcessHandler {
            TODO("Not yet implemented")
        }
    }
}