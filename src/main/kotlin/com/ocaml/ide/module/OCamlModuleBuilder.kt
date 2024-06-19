package com.ocaml.ide.module

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.CompilerModuleExtension
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.platform.ProjectTemplate
import com.ocaml.ide.module.wizard.templates.OCamlTemplateProvider
import com.ocaml.ide.module.wizard.templates.TemplateBuildInstructions
import java.io.File
import java.util.function.Supplier

typealias StringSupplier = () -> String?

open class BaseOCamlModuleBuilder : ModuleBuilder() {
    private var contentEntryPath: StringSupplier? = null

    override fun getModuleType(): ModuleType<*> = OCamlIDEModuleType
    override fun getContentEntryPath(): String? = contentEntryPath!!()
    private val defaultContentEntryPath = { super.getContentEntryPath() }

    fun setupRootModel(
        rootModel: ModifiableRootModel,
        sdkSupplier: Supplier<Sdk?>,
        contentEntryPath: StringSupplier?,
        template: ProjectTemplate?
    ) {
        this.contentEntryPath = contentEntryPath ?: defaultContentEntryPath
        // Create the files/folders
        val contentEntry = doAddContentEntry(rootModel)
        if (contentEntry != null) {
            // Get instructions
            val instructions: TemplateBuildInstructions =
                if (template is TemplateBuildInstructions) template else OCamlTemplateProvider.defaultInstructions

            // create the source folder
            val sourcePath = getContentEntryPath() + File.separator + instructions.sourceFolderName
            File(sourcePath).mkdirs()
            val sourceRoot = LocalFileSystem.getInstance()
                .refreshAndFindFileByPath(FileUtil.toSystemIndependentName(sourcePath))
            if (sourceRoot != null) {
                contentEntry.addSourceFolder(sourceRoot, false, "")
                // create the files
                val sdk = sdkSupplier.get()
                instructions.createFiles(rootModel, sourceRoot, sdk?.homePath)
                // refresh
                ApplicationManager.getApplication().runWriteAction {
                    sourceRoot.refresh(
                        true,
                        true
                    )
                }
            }
        }
    }
}

class OCamlModuleBuilder : BaseOCamlModuleBuilder() {
    private var myTemplate: ProjectTemplate? = null

//    override fun isSuitableSdkType(sdkType: SdkTypeId?): Boolean = sdkType is OCamlSdkType
    fun setProjectTemplate(template: ProjectTemplate?) { myTemplate = template }

    override fun setupRootModel(rootModel: ModifiableRootModel) {
        // output folder
        val compilerModuleExtension = rootModel.getModuleExtension(
            CompilerModuleExtension::class.java
        )
        compilerModuleExtension.isExcludeOutput = true
        compilerModuleExtension.inheritCompilerOutputPath(true)

        // set the SDK "JDK"
        if (myJdk != null) rootModel.sdk = myJdk
        else rootModel.inheritSdk()

        setupRootModel(rootModel, { rootModel.sdk }, null, myTemplate)
    }

    // The options' step is the first step, the user will select an SDK
//    override fun getCustomOptionsStep(context: WizardContext?, parentDisposable: Disposable?): ModuleWizardStep {
//        return OCamlSdkWizardStep(context, this)
//    }

//    /**
//     * Show steps after the custom option steps
//     * and before the project step
//     */
//    fun createWizardSteps(wizardContext: WizardContext?, modulesProvider: ModulesProvider?): Array<ModuleWizardStep> {
//        return arrayOf(OCamlSelectTemplate(wizardContext, OCamlTemplateProvider.availableTemplates))
//    }
}