package com.ocaml.sdk.runConfiguration

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.compiler.options.CompileStepBeforeRun
import com.intellij.diagnostic.logging.LogsGroupFragment
import com.intellij.execution.ExecutionBundle
import com.intellij.execution.JavaRunConfigurationExtensionManager
import com.intellij.execution.application.ClassEditorField
import com.intellij.execution.configurations.ConfigurationUtil
import com.intellij.execution.configurations.RuntimeConfigurationException
import com.intellij.execution.ui.*
import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.Predicates
import com.intellij.psi.JavaCodeFragment
import com.intellij.psi.PsiClass
import com.intellij.psi.impl.java.stubs.index.JavaStubIndexKeys
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.PsiMethodUtil
import com.intellij.ui.EditorTextField
import com.intellij.ui.TextFieldWithAutoCompletion
import com.intellij.ui.TextFieldWithAutoCompletion.StringsCompletionProvider
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.ui.GridBag
import com.intellij.util.ui.UIUtil
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.ActionListener
import java.awt.event.MouseListener
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Supplier
import javax.swing.JComponent
import javax.swing.JPanel

// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
// JavaApplicationSettingsEditor
internal class OCamlConfigurationSettingsEditor(configuration: OCamlRunConfiguration) :
    OCamlRunConfigurationSettingsEditorBase<OCamlRunConfiguration?>(configuration) {
    private var myMainClassFragment: SettingsEditorFragment<OCamlRunConfiguration?, MainClassPanel?>? = null
    private val myInitialIsImplicitClass = configuration.isImplicitClassConfiguration()

    override fun isInplaceValidationSupported(): Boolean {
        return true
    }

    override fun customizeFragments(
        fragments: MutableList<SettingsEditorFragment<OCamlRunConfiguration?, *>>,
        moduleClasspath: SettingsEditorFragment<OCamlRunConfiguration?, ModuleClasspathCombo>,
        commonParameterFragments: CommonParameterFragments<OCamlRunConfiguration?>
    ) {
        fragments.add(
            SettingsEditorFragment.createTag("include.provided",
                ExecutionBundle.message("application.configuration.include.provided.scope"),
                ExecutionBundle.message("group.java.options"),
                { configuration: OCamlRunConfiguration? -> configuration!!.getOptions().isIncludeProvidedScope },
                { configuration: OCamlRunConfiguration?, value: Boolean? ->
                    configuration!!.getOptions().isIncludeProvidedScope =
                        value!!
                })
        )
        fragments.add(
            SettingsEditorFragment.createTag("unnamed.class",
                ExecutionBundle.message("application.configuration.is.implicit.class"),
                ExecutionBundle.message("group.java.options"),
                { configuration: OCamlRunConfiguration? -> configuration!!.isImplicitClassConfiguration() },
                { configuration: OCamlRunConfiguration?, value: Boolean? ->
                    configuration!!.setImplicitClassConfiguration(value!!)
                    updateMainClassFragment(configuration.isImplicitClassConfiguration())
                })
        )
        fragments.add(commonParameterFragments.programArguments())
        fragments.add(TargetPathFragment())
        fragments.add(commonParameterFragments.createRedirectFragment())
        val mainClassFragment = createMainClass(moduleClasspath.component())
        fragments.add(mainClassFragment)
        val jreSelector = DefaultJreSelector.fromSourceRootsDependencies(
            moduleClasspath.component(),
            mainClassFragment.component()!!.editorTextField!!
        )
        val jrePath = CommonJavaFragments.createJrePath<OCamlRunConfiguration?>(jreSelector)
        fragments.add(jrePath)
        fragments.add(createShortenClasspath(moduleClasspath.component(), jrePath, true))
    }

    fun getVisibilityChecker(selector: ConfigurationModuleSelector): JavaCodeFragment.VisibilityChecker {
        return JavaCodeFragment.VisibilityChecker { declaration, place ->
            if (declaration is PsiClass) {
                if (ConfigurationUtil.MAIN_CLASS.value(declaration) && PsiMethodUtil.findMainMethod(declaration) != null ||
                    place?.parent != null && selector.findClass(declaration.qualifiedName) != null) {
                    return@VisibilityChecker JavaCodeFragment.VisibilityChecker.Visibility.VISIBLE
                }
            }
            return@VisibilityChecker JavaCodeFragment.VisibilityChecker.Visibility.NOT_VISIBLE
        }
    }

    private inner class MainClassPanel(classpathCombo: ModuleClasspathCombo) : JPanel(GridBagLayout()) {
        private val myClassEditorField: ClassEditorField?
        private val myImplicitClassField: TextFieldWithAutoCompletion<String>
        private var myIsImplicitClassConfiguration = false

        init {
            CommandLinePanel.setMinimumWidth(this, 300)

            val moduleSelector = ConfigurationModuleSelector(project, classpathCombo)
            myClassEditorField = ClassEditorField.createClassField(
                project,
                { classpathCombo.selectedModule },
                getVisibilityChecker(moduleSelector),
                null
            )
            myClassEditorField.background = UIUtil.getTextFieldBackground()
            myClassEditorField.setShowPlaceholderWhenFocused(true)
            CommonParameterFragments.setMonospaced(myClassEditorField)
            val placeholder = ExecutionBundle.message("application.configuration.main.class.placeholder")
            myClassEditorField.setPlaceholder(placeholder)
            myClassEditorField.accessibleContext.accessibleName = placeholder
            myClassEditorField.isVisible = !myIsImplicitClassConfiguration
            CommandLinePanel.setMinimumWidth(myClassEditorField, 300)
            val constraints = GridBag().setDefaultFill(GridBagConstraints.HORIZONTAL).setDefaultWeightX(1.0)
            add(myClassEditorField, constraints.nextLine())

            myImplicitClassField = TextFieldWithAutoCompletion<String>(
                project,
                object : StringsCompletionProvider(null, AllIcons.FileTypes.JavaClass) {
                    override fun getItems(
                        prefix: String,
                        cached: Boolean,
                        parameters: CompletionParameters
                    ): Collection<String> {
                        return if (DumbService.isDumb(project)
                        ) listOf() else ReadAction.compute<Collection<String>, RuntimeException> {
                            StubIndex.getInstance().getAllKeys(
                                JavaStubIndexKeys.IMPLICIT_CLASSES,
                                project
                            )
                        }
                    }
                },
                true,
                null
            )
            CommonParameterFragments.setMonospaced(myImplicitClassField)
            val implicitClassPlaceholder =
                ExecutionBundle.message("application.configuration.main.unnamed.class.placeholder")
            myImplicitClassField.isVisible = myIsImplicitClassConfiguration
            myImplicitClassField.setPlaceholder(implicitClassPlaceholder)
            myImplicitClassField.accessibleContext.accessibleName = implicitClassPlaceholder
            CommandLinePanel.setMinimumWidth(myImplicitClassField, 300)
            add(myImplicitClassField, constraints.nextLine())
        }

        val editorTextField: EditorTextField?
            get() = myClassEditorField

        var className: String?
            get() = if (myIsImplicitClassConfiguration) myImplicitClassField.text else myClassEditorField!!.className
            set(name) {
                if (myIsImplicitClassConfiguration) {
                    myImplicitClassField.setText(name)
                } else {
                    myClassEditorField!!.className = name
                }
            }

        val isReadyForApply: Boolean
            get() = myIsImplicitClassConfiguration || myClassEditorField!!.isReadyForApply

        fun setImplicitClassConfiguration(isImplicitClassConfiguration: Boolean) {
            myIsImplicitClassConfiguration = isImplicitClassConfiguration
            if (myClassEditorField != null) {
                myClassEditorField.isVisible = !isImplicitClassConfiguration
                myImplicitClassField.isVisible = isImplicitClassConfiguration
            }
        }

        fun getValidation(configuration: OCamlRunConfiguration?): List<ValidationInfo> {
            return listOf(RuntimeConfigurationException.validate<RuntimeConfigurationException>(
                if (myIsImplicitClassConfiguration) myImplicitClassField else myClassEditorField
            ) { if (!isDefaultSettings) configuration!!.checkClass() })
        }

        val editorComponent: JComponent?
            get() {
                if (myIsImplicitClassConfiguration) {
                    return myImplicitClassField
                } else {
                    val editor = myClassEditorField!!.editor
                    return editor?.contentComponent ?: myClassEditorField
                }
            }

        @Synchronized
        override fun addMouseListener(l: MouseListener) {
            myImplicitClassField.addMouseListener(l)
            myClassEditorField!!.addMouseListener(l)
        }
    }

    private fun createMainClass(classpathCombo: ModuleClasspathCombo):
            SettingsEditorFragment<OCamlRunConfiguration?, MainClassPanel?> {
        val mainClassPanel = MainClassPanel(classpathCombo)
        myMainClassFragment =
            object : SettingsEditorFragment<OCamlRunConfiguration?, MainClassPanel?>(
                "mainClass", ExecutionBundle.message("application.configuration.main.class"), null, mainClassPanel, 20,
                BiConsumer { configuration: OCamlRunConfiguration?, component: MainClassPanel? ->
                    mainClassPanel.className = configuration?.getMainClassName()
                },
                BiConsumer { configuration: OCamlRunConfiguration?, component: MainClassPanel? ->
                    configuration?.setMainClassName(mainClassPanel.className)
                },
                Predicates.alwaysTrue<OCamlRunConfiguration>()
            ) {
                override fun isReadyForApply(): Boolean {
                    return myComponent!!.isReadyForApply
                }
            }.apply {
                isRemovable = false
                setEditorGetter { field: MainClassPanel? -> field!!.editorComponent }
                setValidation { configuration: OCamlRunConfiguration? ->
                    mainClassPanel.getValidation(
                        configuration
                    )
                }
            }
        updateMainClassFragment(myInitialIsImplicitClass)
        return myMainClassFragment as SettingsEditorFragment<OCamlRunConfiguration?, MainClassPanel?>
    }

    private fun updateMainClassFragment(isImplicitClass: Boolean) {
        if (myMainClassFragment == null) return
        myMainClassFragment!!.component()!!.setImplicitClassConfiguration(isImplicitClass)

        if (isImplicitClass) {
            myMainClassFragment!!.setHint(ExecutionBundle.message("application.configuration.main.class.unnamed.hint"))
        } else {
            myMainClassFragment!!.setHint(ExecutionBundle.message("application.configuration.main.class.hint"))
        }
    }
}

// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
// JavaSettingsEditorBase
internal abstract class OCamlRunConfigurationSettingsEditorBase<T : OCamlRunConfiguration?>(runConfiguration: T) :
    RunConfigurationFragmentedEditor<T>(runConfiguration, JavaRunConfigurationExtensionManager.instance) {
    override fun createRunFragments(): List<SettingsEditorFragment<T, *>> {
        val fragments: MutableList<SettingsEditorFragment<T, *>> = ArrayList()
        val beforeRunComponent = BeforeRunComponent(this)
        fragments.add(BeforeRunFragment.createBeforeRun(beforeRunComponent, CompileStepBeforeRun.ID))
        fragments.addAll(BeforeRunFragment.createGroup())

        val moduleClasspath = CommonJavaFragments.moduleClasspath<T>()
        val classpathCombo = moduleClasspath.component()
        val hasModule =
            Computable { classpathCombo.selectedModule != null }

        fragments.add(CommonTags.parallelRun())

        val commonParameterFragments = CommonParameterFragments<T>(
            project
        ) { classpathCombo.selectedModule }
        fragments.addAll(commonParameterFragments.fragments)
        fragments.add(CommonJavaFragments.createBuildBeforeRun(beforeRunComponent, this))

        fragments.add(moduleClasspath)
        //fragments.add(ClasspathModifier(mySettings))
        customizeFragments(fragments, moduleClasspath, commonParameterFragments)
        val jrePath = ContainerUtil.find(
            fragments
        ) { f: SettingsEditorFragment<T, *> -> CommonJavaFragments.JRE_PATH == f.id }
        val jrePathEditor: JrePathEditor? =
            if (jrePath != null && jrePath.component is JrePathEditor) jrePath.component as JrePathEditor else null
//        val vmParameters: SettingsEditorFragment<T, *> =
//            CommonJavaFragments.vmOptionsEx(mySettings, hasModule, jrePathEditor)
//        fragments.add(vmParameters)

        fragments.add(LogsGroupFragment())
        return fragments
    }

    protected fun createShortenClasspath(
        classpathCombo: ModuleClasspathCombo,
        jrePath: SettingsEditorFragment<T, JrePathEditor?>,
        productionOnly: Boolean
    ): SettingsEditorFragment<T, LabeledComponent<ShortenCommandLineModeCombo>> {
        val combo: ShortenCommandLineModeCombo = object : ShortenCommandLineModeCombo(
            project, jrePath.component(),
            Supplier { classpathCombo.selectedModule },
            Consumer { listener: ActionListener? ->
                classpathCombo.addActionListener(
                    listener
                )
            }) {
            override fun productionOnly(): Boolean {
                return productionOnly
            }
        }
        val component = LabeledComponent.create(
            combo,
            ExecutionBundle.message(
                "application.configuration.shorten.command.line.label"
            ),
            BorderLayout.WEST
        )
        val fragment =
            SettingsEditorFragment("shorten.command.line",
                ExecutionBundle.message("application.configuration.shorten.command.line"),
                ExecutionBundle.message("group.java.options"),
                component,
                { t: T, c: LabeledComponent<ShortenCommandLineModeCombo> ->
                    c.component.item = t!!.shortenCommandLine
                },
                { t: T, c: LabeledComponent<ShortenCommandLineModeCombo> ->
                    t!!.shortenCommandLine = if (c.isVisible) c.component.selectedItem else null
                },
                { configuration: T -> configuration!!.shortenCommandLine != null })
        fragment.actionHint =
            ExecutionBundle.message("select.a.method.to.shorten.the.command.if.it.exceeds.the.os.limit")
        return fragment
    }

    protected abstract fun customizeFragments(
        fragments: MutableList<SettingsEditorFragment<T, *>>,
        moduleClasspath: SettingsEditorFragment<T, ModuleClasspathCombo>,
        commonParameterFragments: CommonParameterFragments<T>
    )

    override fun targetChanged(targetName: String?) {
        super.targetChanged(targetName)
        val fragment = ContainerUtil.find(
            fragments
        ) { f: SettingsEditorFragment<T?, *> -> CommonJavaFragments.JRE_PATH == f.id }
        if (fragment != null) {
            if ((fragment.component() as JrePathEditor).updateModel(project, targetName)) {
                fragment.resetFrom(mySettings)
            }
        }
    }
}
