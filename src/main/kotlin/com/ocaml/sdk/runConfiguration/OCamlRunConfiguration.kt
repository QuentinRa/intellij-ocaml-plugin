package com.ocaml.sdk.runConfiguration

import com.intellij.execution.*
import com.intellij.execution.InputRedirectAware.InputRedirectOptions
import com.intellij.execution.application.*
import com.intellij.execution.configurations.*
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.impl.statistics.FusAwareRunConfiguration
import com.intellij.execution.junit.RefactoringListeners
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.target.LanguageRuntimeType
import com.intellij.execution.target.TargetEnvironmentAwareRunProfile
import com.intellij.execution.target.TargetEnvironmentConfiguration
import com.intellij.execution.target.getEffectiveTargetName
import com.intellij.execution.target.java.JavaLanguageRuntimeConfiguration
import com.intellij.execution.target.java.JavaLanguageRuntimeType
import com.intellij.execution.util.JavaParametersUtil
import com.intellij.execution.util.ProgramParametersUtil
import com.intellij.execution.util.checkEnvFiles
import com.intellij.execution.vmOptions.VMOption
import com.intellij.execution.vmOptions.VMOption.Companion.property
import com.intellij.execution.wsl.WslPath
import com.intellij.internal.statistic.eventLog.events.EventFields
import com.intellij.internal.statistic.eventLog.events.EventPair
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.IndexNotReadyException
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.NlsContexts.DialogMessage
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.util.registry.Registry
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.*
import com.intellij.psi.impl.java.stubs.index.JavaImplicitClassIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.ClassUtil
import com.intellij.psi.util.PsiMethodUtil
import com.intellij.refactoring.listeners.RefactoringElementListener
import com.intellij.util.PathUtil
import com.intellij.util.containers.ContainerUtil
import org.jdom.Element
import java.util.*

// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
// ApplicationConfiguration
internal class OCamlRunConfiguration(
    name: String?,
    project: Project,
    factory: ConfigurationType
) : ModuleBasedConfiguration<OCamlRunConfigurationModule, Element>(name, OCamlRunConfigurationModule(project), factory.configurationFactories[0]),
    CommonJavaRunConfigurationParameters, ConfigurationWithCommandLineShortener,
    SingleClassConfiguration, RefactoringListenerProvider, InputRedirectAware,
    TargetEnvironmentAwareRunProfile, FusAwareRunConfiguration, EnvFilesOptions
{
    public override fun getOptions(): OCamlRunConfigurationOptions = super.getOptions() as OCamlRunConfigurationOptions

    // tmp
    override fun getClasspathModifications(): MutableList<ModuleBasedConfigurationOptions.ClasspathModification> =
        options.classpathModifications

    override fun setClasspathModifications(modifications: List<ModuleBasedConfigurationOptions.ClasspathModification>) {
        options.classpathModifications = modifications.toMutableList()
    }

    protected fun runsUnderWslJdk(): Boolean {
        val path = alternativeJrePath
        if (path != null) {
            val sdk = ProjectJdkTable.getInstance().findJdk(path)
            if (sdk != null) {
                val homePath = sdk.homePath
                if (homePath != null) {
                    return WslPath.isWslUncPath(homePath)
                }
            }
            return WslPath.isWslUncPath(path)
        }
        val module = configurationModule.module
        if (module != null) {
            val sdk: Sdk
            try {
                sdk = JavaParameters.getValidJdkToRunModule(module, false)
            } catch (e: CantRunException) {
                return false
            }
            val sdkHomePath = sdk.homePath
            return sdkHomePath != null && WslPath.isWslUncPath(sdkHomePath)
        }
        return false
    }

    /**
     * @return list of configuration-specific VM options (usually, -D options), used for completion
     */
    fun getKnownVMOptions(): List<VMOption> {
        return listOf(
            property("java.awt.headless", "bool", "Run the application in headless mode", null),
            property("user.home", "string", "User home directory", null),
            property("user.dir", "string", "User working directory", null),
            property("user.name", "string", "User account name", null)
        )
    }

    // tmp II
    /* deprecated, but 3rd-party used variables */
    @Deprecated("")
    var MAIN_CLASS_NAME: String? = null

    @Deprecated("")
    var PROGRAM_PARAMETERS: String? = null

    @Deprecated("")
    var WORKING_DIRECTORY: String? = null

    @Deprecated("")
    var ALTERNATIVE_JRE_PATH_ENABLED: Boolean = false

    @Deprecated("")
    var ALTERNATIVE_JRE_PATH: String? = null

    override fun setMainClass(psiClass: PsiClass) {
        val originalModule = configurationModule.module
        setMainClassName(JavaExecutionUtil.getRuntimeQualifiedName(psiClass))
        setModule(JavaExecutionUtil.findModule(psiClass))
        restoreOriginalModule(originalModule)
    }

    @Throws(ExecutionException::class)
    override fun getState(executor: Executor, env: ExecutionEnvironment): RunProfileState {
        val state: JavaCommandLineState = JavaApplicationCommandLineState(this, env)
        val module: OCamlRunConfigurationModule = configurationModule
        state.consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(project, module.searchScope)
        return state
    }

    override fun getConfigurationEditor(): OCamlConfigurationSettingsEditor {
        if (Registry.`is`("ide.new.run.config", true)) {
            return OCamlConfigurationSettingsEditor(this)
        }
        error("Not expected.")
//        val group = SettingsEditorGroup<ApplicationConfiguration>()
//        group.addEditor(
//            ExecutionBundle.message("run.configuration.configuration.tab.title"), ApplicationConfigurable(
//                project
//            )
//        )
//        JavaRunConfigurationExtensionManager.instance.appendEditors(this, group)
//        group.addEditor(ExecutionBundle.message("logs.tab.title"), LogConfigurationPanel())
//        return group
    }

    override fun getRefactoringElementListener(element: PsiElement?): RefactoringElementListener {
        val listener = RefactoringListeners.getClassOrPackageListener(element,
            RefactoringListeners.SingleClassConfigurationAccessor(this)
        )
        return RunConfigurationExtension.wrapRefactoringElementListener(element, this, listener)
    }

    override fun getMainClass(): PsiClass? {
        return configurationModule.findClass(getMainClassName())
    }

    fun getMainClassName(): String? {
        return MAIN_CLASS_NAME
    }

    override fun suggestedName(): String? {
        val mainClassName = getMainClassName() ?: return null
        val configName = JavaExecutionUtil.getPresentableClassName(mainClassName)
        if (configName != null) {
            val configuration = RunManager.getInstance(project).findConfigurationByTypeAndName(
                type, configName
            )
            if (configuration != null) {
                val thatConfig = configuration.configuration
                if (thatConfig is ApplicationConfiguration &&
                    thatConfig.mainClassName != mainClassName
                ) {
                    return mainClassName
                }
            }
        }
        return configName
    }

    override fun getActionName(): String? {
        if (getMainClassName() == null) {
            return null
        }
        val mainSuffix = ".main()"
        return ProgramRunnerUtil.shortenName(JavaExecutionUtil.getShortClassName(getMainClassName()), 6) + mainSuffix
    }

    override fun setMainClassName(qualifiedName: String?) {
        MAIN_CLASS_NAME = qualifiedName
        options.mainClassName = qualifiedName
    }

    @Throws(RuntimeConfigurationException::class)
    override fun checkConfiguration() {
        if (this.getEffectiveTargetName(project) == null) {
            JavaParametersUtil.checkAlternativeJRE(this)
        }
        val configurationModule = checkClass()
        ProgramParametersUtil.checkWorkingDirectoryExist(this, project, configurationModule.module)
        checkEnvFiles(this)
        JavaRunConfigurationExtensionManager.checkConfigurationIsValid(this)
    }

    @Throws(RuntimeConfigurationException::class)
    fun checkClass(): OCamlRunConfigurationModule {
        val configurationModule: OCamlRunConfigurationModule = configurationModule
        val mainClass = getMainClassName()
        if (options.isImplicitClassConfiguration) {
            if (mainClass != null && !DumbService.isDumb(project)) {
                try {
                    val matchingClass = !JavaImplicitClassIndex.getInstance()
                        .getElements(mainClass, project, configurationModule.searchScope)
                        .isEmpty()
                    if (!matchingClass) {
                        throw RuntimeConfigurationWarning(
                            ExecutionBundle.message(
                                "main.method.not.found.in.class.error.message",
                                mainClass
                            )
                        )
                    }
                } catch (ignored: IndexNotReadyException) {
                }
            }
        } else {
            val psiClass = configurationModule.checkModuleAndClassName(
                mainClass,
                ExecutionBundle.message("no.main.class.specified.error.text")
            )
            if (psiClass == null || !PsiMethodUtil.hasMainMethod(psiClass)) {
                throw RuntimeConfigurationWarning(
                    ExecutionBundle.message(
                        "main.method.not.found.in.class.error.message",
                        mainClass
                    )
                )
            }
        }
        return configurationModule
    }

    override fun setVMParameters(value: String?) {
        options.vmParameters = value
    }

    override fun getVMParameters(): String? {
        return options.vmParameters
    }

    override fun setProgramParameters(value: String?) {
        PROGRAM_PARAMETERS = value
        options.programParameters = value
    }

    override fun getProgramParameters(): String? {
        return PROGRAM_PARAMETERS
    }

    override fun setWorkingDirectory(value: String?) {
        val normalizedValue = if (StringUtil.isEmptyOrSpaces(value)) null else value!!.trim { it <= ' ' }
        WORKING_DIRECTORY = PathUtil.toSystemDependentName(normalizedValue)

        val independentValue = PathUtil.toSystemIndependentName(normalizedValue)
        options.workingDirectory = if (independentValue == project.basePath) null else independentValue
    }

    override fun getWorkingDirectory(): String? {
        return WORKING_DIRECTORY
    }

    override fun setPassParentEnvs(value: Boolean) {
        options.isPassParentEnv = value
    }

    override fun getEnvs(): Map<String, String> {
        return options.env
    }

    override fun setEnvs(envs: Map<String, String>) {
        options.env = envs.toMutableMap()
    }

    override fun isPassParentEnvs(): Boolean {
        return options.isPassParentEnv
    }

    override var envFilePaths: List<String>
        get() = options.envFilePaths
        set(value) { options.envFilePaths = value.toMutableList() }

    override fun getRunClass(): String? {
        return getMainClassName()
    }

    override fun getPackage(): String? {
        return null
    }

    override fun isAlternativeJrePathEnabled(): Boolean {
        return ALTERNATIVE_JRE_PATH_ENABLED
    }

    override fun setAlternativeJrePathEnabled(enabled: Boolean) {
        val changed = ALTERNATIVE_JRE_PATH_ENABLED != enabled
        ALTERNATIVE_JRE_PATH_ENABLED = enabled
        options.isAlternativeJrePathEnabled = enabled
        onAlternativeJreChanged(changed, project)
    }

    override fun getAlternativeJrePath(): String? {
        return ALTERNATIVE_JRE_PATH
    }

    override fun setAlternativeJrePath(path: String?) {
        val changed = ALTERNATIVE_JRE_PATH != path
        ALTERNATIVE_JRE_PATH = path
        options.alternativeJrePath = path
        onAlternativeJreChanged(changed, project)
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

    override fun getAdditionalUsageData(): List<EventPair<*>> {
        val mainClass = mainClass
        val additionalUsageData = super.getAdditionalUsageData()
        if (mainClass == null) {
            return additionalUsageData
        }
        return ContainerUtil.concat(
            additionalUsageData,
            listOf(EventFields.Language.with(mainClass.language))
        )
    }

    fun onAlternativeJreChanged(changed: Boolean, project: Project?) {
        if (changed) {
            AlternativeSdkRootsProvider.reindexIfNeeded(project!!)
        }
    }

    fun isProvidedScopeIncluded(): Boolean {
        return options.isIncludeProvidedScope
    }

    fun setIncludeProvidedScope(value: Boolean) {
        options.isIncludeProvidedScope = value
    }

    fun isImplicitClassConfiguration(): Boolean {
        return options.isImplicitClassConfiguration
    }

    fun setImplicitClassConfiguration(value: Boolean) {
        options.isImplicitClassConfiguration = value
    }

    override fun getValidModules(): Collection<Module> {
        return OCamlRunConfigurationModule.getModulesForClass(project, getMainClassName())
    }

    override fun readExternal(element: Element) {
        super<ModuleBasedConfiguration>.readExternal(element)

        syncOldStateFields()

        JavaRunConfigurationExtensionManager.instance.readExternal(this, element)
    }

    private fun syncOldStateFields() {
        val options: JvmMainMethodRunConfigurationOptions = options

        var workingDirectory = options.workingDirectory
        workingDirectory = if (workingDirectory == null) {
            PathUtil.toSystemDependentName(project.basePath)
        } else {
            FileUtilRt.toSystemDependentName(VirtualFileManager.extractPath(workingDirectory))
        }

        MAIN_CLASS_NAME = options.mainClassName
        PROGRAM_PARAMETERS = options.programParameters
        WORKING_DIRECTORY = workingDirectory
        ALTERNATIVE_JRE_PATH = options.alternativeJrePath
        ALTERNATIVE_JRE_PATH_ENABLED = options.isAlternativeJrePathEnabled
    }

    override fun setOptionsFromConfigurationFile(state: BaseState) {
        super.setOptionsFromConfigurationFile(state)
        syncOldStateFields()
    }

    override fun writeExternal(element: Element) {
        super<ModuleBasedConfiguration>.writeExternal(element)
        JavaRunConfigurationExtensionManager.instance.writeExternal(this, element)
    }

    override fun getShortenCommandLine(): ShortenCommandLine? {
        return options.shortenClasspath
    }

    override fun setShortenCommandLine(mode: ShortenCommandLine?) {
        options.shortenClasspath = mode
    }

    override fun getInputRedirectOptions(): InputRedirectOptions {
        return options.redirectOptions
    }

    override fun getDefaultModule(): Module {
        if (ModuleManager.getInstance(project).modules.size < 2) {
            return super.getDefaultModule()
        }
        val mainClass = mainClass
        if (mainClass != null) {
            val module = ModuleUtilCore.findModuleForPsiElement(mainClass)
            if (module != null) return module
        }
        return super.getDefaultModule()
    }

    class JavaApplicationCommandLineState(
        configuration: OCamlRunConfiguration,
        environment: ExecutionEnvironment?
    ) : BaseJavaApplicationCommandLineState<OCamlRunConfiguration>(environment, configuration) {

        @Throws(ExecutionException::class)
        override fun createJavaParameters(): JavaParameters {
            val params = JavaParameters()
            val configuration = configuration
            params.mainClass = ReadAction.compute<String?, RuntimeException> { myConfiguration.runClass }
            params.setShortenCommandLine(configuration.getShortenCommandLine(), configuration.getProject())
            setupJavaParameters(params)
            return params
        }

        override fun isReadActionRequired(): Boolean {
            return false
        }
    }
}

internal class OCamlRunConfigurationModule(project: Project, private val myClassesInLibraries: Boolean = false)
    : RunConfigurationModule(project) {

    fun findClass(qualifiedName: String?): PsiClass? {
        if (qualifiedName == null) return null
        val project = project
        val searchScope = searchScope
        val mainClass = JavaExecutionUtil.findMainClass(project, qualifiedName, searchScope)
        if (mainClass == null && !PsiNameHelper.getInstance(project).isQualifiedName(qualifiedName)) {
            return findClass(
                StringUtil.getShortName(qualifiedName),
                StringUtil.getPackageName(qualifiedName),
                project,
                searchScope
            )
        }
        return mainClass
    }

    private fun findClass(
        shortName: String,
        packageName: String,
        project: Project,
        searchScope: GlobalSearchScope
    ): PsiClass? {
        val aPackage = JavaPsiFacade.getInstance(project).findPackage(packageName)
        if (aPackage != null) {
            val dollarIdx = shortName.indexOf("$")
            val topLevelClassName =
                if (dollarIdx > 0 && dollarIdx < shortName.length - 1) shortName.substring(0, dollarIdx) else shortName
            val topLevelClass = ContainerUtil.find(aPackage.getClasses(searchScope),
                { aClass: PsiClass -> (topLevelClassName == aClass.getName()) })
            if (topLevelClass != null && !(topLevelClassName == shortName)) {
                val innerClassName = shortName.substring(dollarIdx + 1)
                return ClassUtil.findPsiClass(PsiManager.getInstance(project), innerClassName, topLevelClass, true)
            }
            return topLevelClass
        }

        if (packageName.isEmpty()) {
            return null
        }

        val topClass = findClass(
            StringUtil.getShortName(packageName),
            StringUtil.getPackageName(packageName),
            project,
            searchScope
        )
        if (topClass != null) {
            return topClass.findInnerClassByName(shortName, true)
        }
        return null
    }

    val searchScope: GlobalSearchScope
        get() {
            val module = module
            if (module != null) {
                return if (myClassesInLibraries) module.getModuleRuntimeScope(true) else GlobalSearchScope.moduleWithDependenciesScope(
                    module
                )
            } else {
                return if (myClassesInLibraries) GlobalSearchScope.allScope(project) else GlobalSearchScope.projectScope(
                    project
                )
            }
        }

    companion object {
        fun getModulesForClass(project: Project, className: String?): Collection<Module> {
            if (project.isDefault) {
                return Arrays.asList(*ModuleManager.getInstance(project).modules)
            }
            PsiDocumentManager.getInstance(project).commitAllDocuments()
            val possibleClasses = if (className == null) PsiClass.EMPTY_ARRAY else JavaPsiFacade.getInstance(project)
                .findClasses(className, GlobalSearchScope.projectScope(project))
            val modules: MutableSet<Module> = HashSet()
            for (aClass: PsiClass? in possibleClasses) {
                val module = ModuleUtilCore.findModuleForPsiElement((aClass)!!)
                if (module != null) {
                    modules.add(module)
                }
            }
            if (modules.isEmpty()) {
                return Arrays.asList(*ModuleManager.getInstance(project).modules)
            } else {
                val result = HashSet<Module>()
                for (module: Module? in modules) {
                    ModuleUtilCore.collectModulesDependsOn((module)!!, result)
                }
                return result
            }
        }
    }

    @Throws(RuntimeConfigurationWarning::class)
    fun findNotNullClass(className: String?): PsiClass {
        val psiClass = findClass(className)
            ?: throw object : RuntimeConfigurationWarning(
                ExecutionBundle.message("class.not.found.in.module.error.message", className, moduleName)
            ) {
                override fun shouldShowInDumbMode(): Boolean {
                    return false
                }
            }
        return psiClass
    }

    @Throws(RuntimeConfigurationException::class)
    fun checkModuleAndClassName(className: String?, expectedClassMessage: @DialogMessage String?): PsiClass {
        checkForWarning()
        return checkClassName(className, expectedClassMessage)
    }

    @Throws(RuntimeConfigurationException::class)
    fun checkClassName(className: String?, errorMessage: @DialogMessage String?): PsiClass {
        if (className == null || className.length == 0) {
            throw RuntimeConfigurationError(errorMessage)
        }
        return findNotNullClass(className)
    }
}