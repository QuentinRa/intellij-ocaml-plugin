package com.ocaml.ide.module.wizard

import com.intellij.ide.projectWizard.ProjectWizardJdkIntent
import com.intellij.ide.projectWizard.generators.AssetsJavaNewProjectWizardStep
import com.intellij.ide.projectWizard.generators.IntelliJNewProjectWizardStep
import com.intellij.ide.starters.local.StandardAssetsProvider
import com.intellij.ide.util.projectWizard.JavaModuleBuilder
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.COLUMNS_LARGE
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.columns
import com.ocaml.OCamlBundle.message
import com.ocaml.ide.module.BaseOCamlModuleBuilder
import com.ocaml.ide.module.OCamlModuleBuilder
import com.ocaml.ide.module.wizard.ui.OCamlProjectWizardJdkComboBox

open class OCamlNewProjectWizardBaseStep(parent: OCamlNewProjectWizard.OCamlNewProjectWizardStep) :
    IntelliJNewProjectWizardStep<OCamlNewProjectWizard.OCamlNewProjectWizardStep>(parent) {

    override fun setupSettingsUI(builder: Panel) {
        builder.row(message("project.wizard.ocaml.sdk")) {
            val combo = OCamlProjectWizardJdkComboBox(context.projectJdk, context.disposable)
            cell(combo)
                .columns(COLUMNS_LARGE)
                .apply {
                    val commentCell = comment(component.comment, 50)
                    component.addItemListener {
                        commentCell.comment?.let { it.text = component.comment }
                    }
                }
                .onChanged {
                    val sdk = when (val intent = combo.selectedItem) {
                        is ProjectWizardJdkIntent.ExistingJdk -> intent.jdk
                        else -> null
                    }
                    sdkProperty.set(sdk)
                }
        }.bottomGap(BottomGap.SMALL)
    }

    override fun setupProject(project: Project) {
        super.setupProject(project)
        setupProject(project, OCamlModuleBuilder())
    }
}

open class OCamlNewProjectWizardAssetStep(private val parent: OCamlNewProjectWizardBaseStep) : AssetsJavaNewProjectWizardStep(parent) {

    override fun setupAssets(project: Project) {
        outputDirectory = parent.contentRoot

        if (context.isCreatingNewProject) {
            addAssets(StandardAssetsProvider().getIntelliJIgnoreAssets())
        }

//        if (parent.addSampleCode) {
//            withJavaSampleCodeAsset("src", "", parent.generateOnboardingTips)
//        }
    }

    override fun setupProject(project: Project) {
        if (parent.generateOnboardingTips) {
            prepareOnboardingTips(project)
        }
        super.setupProject(project)
    }
}