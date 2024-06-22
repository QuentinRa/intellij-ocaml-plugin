// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.ocaml.sdk.runConfiguration

import com.intellij.application.options.ModuleDescriptionsComboBox
import com.intellij.execution.ExecutionBundle
import com.intellij.execution.application.ClassEditorField
import com.intellij.execution.ui.ClassBrowser
import com.intellij.execution.ui.ClassBrowser.MainClassBrowser
import com.intellij.execution.ui.CommonJavaParametersPanel
import com.intellij.execution.ui.ConfigurationModuleSelector
import com.intellij.ide.projectView.impl.nodes.ClassTreeNode
import com.intellij.ide.util.ClassFilter
import com.intellij.ide.util.ClassFilter.ClassFilterWithScope
import com.intellij.ide.util.TreeClassChooser
import com.intellij.ide.util.TreeJavaClassChooserDialog
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.util.Computable
import com.intellij.psi.JavaCodeFragment
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiMethodUtil
import com.intellij.ui.PanelWithAnchor
import com.intellij.util.ui.UIUtil
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode

class OCamlRunConfigurationEditor(private val project: Project) : SettingsEditor<OCamlRunConfiguration>(),
    PanelWithAnchor {
    private var mainPanel: JPanel? = null
    private var mainClass: LabeledComponent<ClassEditorField>? = null

    private var commonProgramParameters: CommonJavaParametersPanel? = null
    private var moduleChooser: LabeledComponent<ModuleDescriptionsComboBox>? = null

    private var anchor: JComponent

    private val moduleSelector = ConfigurationModuleSelector(project, moduleChooser!!.component)

    init {
        commonProgramParameters!!.setModuleContext(moduleSelector.module)
        moduleChooser!!.component.addActionListener { commonProgramParameters!!.setModuleContext(moduleSelector.module) }
        anchor = UIUtil.mergeComponentsWithAnchor(
            mainClass, commonProgramParameters, moduleChooser
        )!!
    }

    override fun applyEditorTo(configuration: OCamlRunConfiguration) {
        commonProgramParameters!!.applyTo(configuration)
        moduleSelector.applyTo(configuration)

        configuration.runClass = mainClass!!.component.className
    }

    override fun resetEditorFrom(configuration: OCamlRunConfiguration) {
        commonProgramParameters!!.reset(configuration)
        moduleSelector.reset(configuration)
        val runClass = configuration.runClass
        mainClass!!.component.text = runClass?.replace("\\$".toRegex(), "\\.") ?: ""
    }

    override fun createEditor(): JComponent = mainPanel!!

    private fun createUIComponents() {
        mainClass = LabeledComponent()
        mainClass!!.setComponent(ClassEditorField.createClassField(
            project,
            { moduleSelector.module },
            { _: PsiElement?, _: PsiElement? -> JavaCodeFragment.VisibilityChecker.Visibility.NOT_VISIBLE },
            createApplicationClassBrowser(
                project, { moduleSelector.module }, moduleChooser
            )
        ))
    }

    override fun getAnchor(): JComponent = anchor

    override fun setAnchor(anchor: JComponent?) {
        this.anchor = anchor!!
        mainClass!!.anchor = anchor
        commonProgramParameters!!.anchor = anchor
        moduleChooser!!.anchor = anchor
    }

    companion object {
        private fun createApplicationClassBrowser(
            project: Project,
            moduleSelector: Computable<out Module>,
            moduleChooser: LabeledComponent<ModuleDescriptionsComboBox>?
        ): ClassBrowser<*> {
            val applicationClass: ClassFilter = object : ClassFilter {
                override fun isAccepted(aClass: PsiClass): Boolean {
//                return aClass instanceof KtLightClass && ConfigurationUtil.MAIN_CLASS.value(aClass) && findMainMethod(aClass) != null;
                    return true
                }

                private fun findMainMethod(aClass: PsiClass): PsiMethod? {
                    return ReadAction.compute<PsiMethod?, RuntimeException> { PsiMethodUtil.findMainMethod(aClass) }
                }
            }
            return object : MainClassBrowser<JComponent>(
                project,
                moduleSelector,
                ExecutionBundle.message("choose.main.class.dialog.title")
            ) {
                override fun findClass(className: String?): PsiClass? {
//                    val configurationModule = OCamlRunConfigurationModule(getProject(), false)
//                    configurationModule.module = moduleSelector.get()
//                    return configurationModule.findClass(className)
                    return null
                }

                override fun createFilter(module: Module?): ClassFilter? {
                    return applicationClass
                }

                override fun onClassChosen(psiClass: PsiClass) {
                    val module = ModuleUtilCore.findModuleForPsiElement(psiClass)
                    if (module != null && ModuleTypeManager.getInstance().isClasspathProvider(ModuleType.get(module))) {
                        moduleChooser!!.component.selectedModule = module
                    }
                }

                override fun createClassChooser(classFilter: ClassFilterWithScope): TreeClassChooser {
                    val project = getProject()
                    return object :
                        TreeJavaClassChooserDialog(myTitle, project, classFilter.scope, classFilter, null, null, true) {
                        override fun getSelectedFromTreeUserObject(node: DefaultMutableTreeNode): PsiClass? {
                            val userObject = node.userObject
                            if (userObject is ClassTreeNode) {
                                return userObject.psiClass
                            }
                            //                        KtElement ktElement = null;
//                        if (userObject instanceof KtFileTreeNode treeNode) {
//                            ktElement = treeNode.getKtFile();
//                        }
//                        if (userObject instanceof KtDeclarationTreeNode treeNode) {
//                            ktElement = treeNode.getDeclaration();
//                        }
//
//                        if (ktElement != null) {
//                            List<PsiNamedElement> elements = LightClassUtilsKt.toLightElements(ktElement);
//                            if (!elements.isEmpty()) {
//                                PsiNamedElement element = elements.get(0);
//                                PsiClass parent = PsiUtilsKt.getParentOfTypes(element, false, PsiClass.class);
//                                if (parent != null) {
//                                    return parent;
//                                }
//                            }
//                        }
                            return null
                        }
                    }
                }
            }
        }
    }
}