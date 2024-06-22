// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.ocaml.sdk.runConfiguration

import com.intellij.codeInsight.daemon.impl.analysis.JavaModuleGraphUtil
import com.intellij.diagnostic.logging.LogConfigurationPanel
import com.intellij.execution.*
import com.intellij.execution.InputRedirectAware.InputRedirectOptions
import com.intellij.execution.application.BaseJavaApplicationCommandLineState
import com.intellij.execution.application.JvmMainMethodRunConfigurationOptions
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.target.LanguageRuntimeType
import com.intellij.execution.target.TargetEnvironmentAwareRunProfile
import com.intellij.execution.target.TargetEnvironmentConfiguration
import com.intellij.execution.target.java.JavaLanguageRuntimeConfiguration
import com.intellij.execution.target.java.JavaLanguageRuntimeType
import com.intellij.execution.util.JavaParametersUtil
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.options.SettingsEditorGroup
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.projectRoots.JavaSdkVersion
import com.intellij.openapi.projectRoots.ex.JavaSdkUtil
import com.intellij.openapi.util.WriteExternalException
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.*
import com.intellij.psi.search.ExecutionSearchScopes
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.refactoring.listeners.RefactoringElementListener
import com.intellij.util.PathUtil
import org.jdom.Element

open class OCamlRunConfiguration(name: String?, runConfigurationModule: JavaRunConfigurationModule, factory: ConfigurationFactory?) :
    JavaRunConfigurationBase(name, runConfigurationModule, factory!!),
    CommonJavaRunConfigurationParameters, RefactoringListenerProvider, InputRedirectAware, TargetEnvironmentAwareRunProfile {

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

    override fun getOptions(): JvmMainMethodRunConfigurationOptions {
        return super.getOptions() as JvmMainMethodRunConfigurationOptions
    }

    override fun readExternal(element: Element) {
        super<JavaRunConfigurationBase>.readExternal(element)
        JavaRunConfigurationExtensionManager.instance.readExternal(this, element)
    }

    @Throws(WriteExternalException::class)
    override fun writeExternal(element: Element) {
        super<JavaRunConfigurationBase>.writeExternal(element)
        JavaRunConfigurationExtensionManager.instance.writeExternal(this, element)
    }

    override fun setWorkingDirectory(value: String?) {
        val normalizedValue = if (StringUtil.isEmptyOrSpaces(value)) null else value!!.trim { it <= ' ' }
        val independentValue = PathUtil.toSystemIndependentName(normalizedValue)
        options.workingDirectory = independentValue?.takeIf { it != defaultWorkingDirectory() }
    }

    override fun getWorkingDirectory(): String? =
        options.workingDirectory?.let {
            FileUtilRt.toSystemDependentName(VirtualFileManager.extractPath(it))
        } ?: PathUtil.toSystemDependentName(defaultWorkingDirectory())

    protected open fun defaultWorkingDirectory() = project.basePath

    override fun setVMParameters(value: String?) {
        options.vmParameters = value
    }

    override fun getVMParameters(): String? {
        return options.vmParameters
    }

    override fun setProgramParameters(value: String?) {
        options.programParameters = value
    }

    override fun getProgramParameters(): String? {
        return options.programParameters
    }

    override fun setPassParentEnvs(passParentEnvs: Boolean) {
        options.isPassParentEnv = passParentEnvs
    }

    override fun isPassParentEnvs(): Boolean {
        return options.isPassParentEnv
    }

    override fun getEnvs(): Map<String, String> {
        return options.env
    }

    override fun setEnvs(envs: Map<String, String>) {
        options.env = envs.toMutableMap()
    }

    override fun getRunClass(): String? {
        return options.mainClassName
    }

    fun setRunClass(value: String?) {
        options.mainClassName = value
    }

    override fun getPackage(): String? {
        return null
    }

    override fun isAlternativeJrePathEnabled(): Boolean {
        return options.isAlternativeJrePathEnabled
    }

    override fun setAlternativeJrePathEnabled(enabled: Boolean) {
        options.isAlternativeJrePathEnabled = enabled
    }

    override fun getAlternativeJrePath(): String? {
        return options.alternativeJrePath
    }

    override fun setAlternativeJrePath(path: String?) {
        options.alternativeJrePath = path
    }

    @Throws(RuntimeConfigurationException::class)
    override fun checkConfiguration() {
        // fixme: no checks for now
    }

    @Throws(ExecutionException::class)
    override fun getState(executor: Executor, executionEnvironment: ExecutionEnvironment): RunProfileState? {
        return MyJavaCommandLineState(this, executionEnvironment)
    }

    override fun getRefactoringElementListener(element: PsiElement): RefactoringElementListener? {
        // fixme: usage?
        return null
    }

    override fun suggestedName(): String? {
        val runClass = runClass
        if (StringUtil.isEmpty(runClass)) {
            return null
        }
        val parts = StringUtil.split(runClass!!, ".")
        return if (parts.isEmpty()) {
            runClass
        } else parts[parts.size - 1]
    }

    override fun getInputRedirectOptions(): InputRedirectOptions {
        return options.redirectOptions
    }

    override fun canRunOn(target: TargetEnvironmentConfiguration): Boolean {
        return target.runtimes.findByType(JavaLanguageRuntimeConfiguration::class.java) != null
    }

    override fun getDefaultLanguageRuntimeType(): LanguageRuntimeType<*>? {
        return LanguageRuntimeType.EXTENSION_NAME.findExtension(JavaLanguageRuntimeType::class.java)
    }

    override fun getDefaultTargetName(): String? {
        return options.remoteTarget
    }

    override fun setDefaultTargetName(targetName: String?) {
        options.remoteTarget = targetName
    }

    override fun needPrepareTarget(): Boolean {
        return super.needPrepareTarget() || runsUnderWslJdk()
    }

    override fun getShortenCommandLine(): ShortenCommandLine? {
        return options.shortenClasspath
    }

    override fun setShortenCommandLine(mode: ShortenCommandLine?) {
        options.shortenClasspath = mode
    }

    private class MyJavaCommandLineState(configuration: OCamlRunConfiguration, environment: ExecutionEnvironment?) :
        BaseJavaApplicationCommandLineState<OCamlRunConfiguration?>(environment, configuration) {
        @Throws(ExecutionException::class)
        override fun createJavaParameters(): JavaParameters {
            val params = JavaParameters()
            val module = myConfiguration.configurationModule
            val classPathType = runReadAction {
                DumbService.getInstance(module!!.project).computeWithAlternativeResolveEnabled<Int, Exception> {
                    getClasspathType(module)
                }
            }
            val jreHome = if (myConfiguration.isAlternativeJrePathEnabled) myConfiguration.alternativeJrePath else null
            JavaParametersUtil.configureConfiguration(params, myConfiguration)
            runReadAction { JavaParametersUtil.configureModule(module, params, classPathType, jreHome) }
            setupJavaParameters(params)
            params.setShortenCommandLine(myConfiguration.shortenCommandLine, module.project)
            params.mainClass = myConfiguration.runClass
            runReadAction { setupModulePath(params, module) }
            return params
        }

        override fun isReadActionRequired(): Boolean {
            return false
        }

        private fun getClasspathType(configurationModule: RunConfigurationModule?): Int {
            return JavaParameters.JDK_AND_CLASSES
        }

        companion object {
            private fun setupModulePath(params: JavaParameters, module: JavaRunConfigurationModule?) {
                if (JavaSdkUtil.isJdkAtLeast(params.jdk, JavaSdkVersion.JDK_1_9)) {
                    DumbService.getInstance(module!!.project).computeWithAlternativeResolveEnabled<PsiJavaModule?, Exception> {
                        JavaModuleGraphUtil.findDescriptorByElement(module.findClass(params.mainClass))
                    }?.let { mainModule ->
                        params.moduleName = mainModule.name
                        JavaParametersUtil.putDependenciesOnModulePath(params, mainModule, false)
                    }
                }
            }
        }
    }
}