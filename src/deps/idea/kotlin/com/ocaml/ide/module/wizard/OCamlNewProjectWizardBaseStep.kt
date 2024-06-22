package com.ocaml.ide.module.wizard

import com.intellij.ide.projectWizard.ProjectWizardJdkIntent
import com.intellij.ide.projectWizard.generators.AssetsJavaNewProjectWizardStep
import com.intellij.ide.projectWizard.generators.IntelliJNewProjectWizardStep
import com.intellij.ide.starters.local.StandardAssetsProvider
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.runBlockingCancellable
import com.intellij.openapi.progress.runBlockingMaybeCancellable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.platform.ide.progress.runWithModalProgressBlocking
import com.intellij.platform.ide.progress.withBackgroundProgress
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.COLUMNS_LARGE
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.columns
import com.ocaml.OCamlBundle.message
import com.ocaml.ide.module.OCamlModuleBuilder
import com.ocaml.ide.module.wizard.buildSystem.OCamlDuneBuildSystemWizard
import com.ocaml.ide.module.wizard.buildSystem.OCamlMakefileBuildSystemWizard
import com.ocaml.ide.module.wizard.templates.OCamlDuneTemplate
import com.ocaml.ide.module.wizard.templates.OCamlMakefileTemplate
import com.ocaml.ide.module.wizard.templates.OCamlTemplateProvider
import com.ocaml.ide.module.wizard.templates.TemplateBuildInstructions
import com.ocaml.ide.module.wizard.ui.OCamlProjectWizardJdkComboBox
import java.io.File

open class OCamlNewProjectWizardBaseStep(parent: OCamlNewProjectWizard.OCamlNewProjectWizardStep) :
    IntelliJNewProjectWizardStep<OCamlNewProjectWizard.OCamlNewProjectWizardStep>(parent) {

    override fun setupSettingsUI(builder: Panel) {
        // Show SDK
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
                .onChanged { loadSdkFromCombobox(combo) }

            // load the initial value
            loadSdkFromCombobox(combo)
        }.bottomGap(BottomGap.SMALL)

        // Show Generate Sample
         setupSampleCodeUI(builder)
    }

    private fun loadSdkFromCombobox(combo: OCamlProjectWizardJdkComboBox) {
        val sdk = when (val intent = combo.selectedItem) {
            is ProjectWizardJdkIntent.ExistingJdk -> intent.jdk
            else -> null
        }
        sdkProperty.set(sdk)
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

        if (parent.addSampleCode) {
            withOCamlSampleCodeAsset(project)
        }
    }

    private fun withOCamlSampleCodeAsset(project: Project) {
        val template = when (parent.parent.buildSystem) {
            OCamlDuneBuildSystemWizard.NAME -> OCamlDuneTemplate()
            OCamlMakefileBuildSystemWizard.NAME -> OCamlMakefileTemplate()
            else -> null
        }

        // Get instructions
        val instructions: TemplateBuildInstructions =
            if (template is TemplateBuildInstructions) template
            else OCamlTemplateProvider.defaultInstructions

        val sourcePath = outputDirectory + File.separator + TemplateBuildInstructions.sourceFolderName
        val sourceRoot = LocalFileSystem.getInstance()
            .refreshAndFindFileByPath(FileUtil.toSystemIndependentName(sourcePath))
        sourceRoot?.let {
            val sdk = parent.sdk
            instructions.createFiles(sourceRoot, sdk?.homePath)
            sourceRoot.refresh(
                true,
                true
            )
        }
    }

    override fun setupProject(project: Project) {
        if (parent.generateOnboardingTips) {
            prepareOnboardingTips(project)
        }
        super.setupProject(project)
    }
}