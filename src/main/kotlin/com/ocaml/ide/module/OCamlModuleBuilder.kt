package com.ocaml.ide.module

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.platform.ProjectTemplate
import com.ocaml.ide.module.wizard.templates.OCamlTemplateProvider
import com.ocaml.ide.module.wizard.templates.TemplateBuildInstructions
import com.ocaml.sdk.OCamlSdkType
import java.io.File
import java.util.function.Supplier

typealias StringSupplier = () -> String?

open class BaseOCamlModuleBuilder : ModuleBuilder() {
    private var contentEntryPath: StringSupplier? = null

    override fun isSuitableSdkType(sdkType: SdkTypeId?): Boolean = sdkType is OCamlSdkType
    override fun getModuleType(): ModuleType<*> = OCamlIdeaModuleType
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