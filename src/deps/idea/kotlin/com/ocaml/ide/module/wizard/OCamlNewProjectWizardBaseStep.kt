package com.ocaml.ide.module.wizard

import com.intellij.ide.projectWizard.ProjectWizardJdkIntent
import com.intellij.ide.projectWizard.generators.AssetsJavaNewProjectWizardStep
import com.intellij.ide.projectWizard.generators.IntelliJNewProjectWizardStep
import com.intellij.ide.starters.local.StandardAssetsProvider
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.COLUMNS_LARGE
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.columns
import com.ocaml.OCamlBundle.message
import com.ocaml.ide.module.OCamlModuleBuilder
import com.ocaml.ide.module.wizard.ui.OCamlProjectWizardJdkComboBox

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
        // setupSampleCodeUI(builder)
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

// Show the button again too
//        if (parent.addSampleCode) {
//            withOCamlSampleCodeAsset("src", parent.parent.buildSystem)
//        }
    }

    private fun withOCamlSampleCodeAsset(sourceRootPath: String, buildSystem: String) {
//        val template = when(buildSystem) {
//            OCamlDuneBuildSystemWizard.NAME -> OCamlDuneTemplate()
//            OCamlMakefileBuildSystemWizard.NAME -> OCamlMakefileTemplate()
//            else -> null
//        }
//        if (contentEntry != null && addSampleCode) {
//            // Get instructions
//            val instructions: TemplateBuildInstructions =
//                if (template is TemplateBuildInstructions) template as TemplateBuildInstructions
//                else OCamlTemplateProvider.defaultInstructions
//
//            // create the source folder
//            val sourcePath = getContentEntryPath() + File.separator + instructions.sourceFolderName
//            File(sourcePath).mkdirs()
//            val sourceRoot = LocalFileSystem.getInstance()
//                .refreshAndFindFileByPath(FileUtil.toSystemIndependentName(sourcePath))
//            if (sourceRoot != null) {
//                contentEntry.addSourceFolder(sourceRoot, false, "")
//                // create the files
//                val sdk = sdkSupplier.get()
//                instructions.createFiles(rootModel, sourceRoot, sdk?.homePath)
//                // refresh
//                ApplicationManager.getApplication().runWriteAction {
//                    sourceRoot.refresh(
//                        true,
//                        true
//                    )
//                }
//            }
//        }
    }

    override fun setupProject(project: Project) {
        if (parent.generateOnboardingTips) {
            prepareOnboardingTips(project)
        }
        super.setupProject(project)
    }
}