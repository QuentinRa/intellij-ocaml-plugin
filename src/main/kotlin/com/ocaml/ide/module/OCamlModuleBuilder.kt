package com.ocaml.ide.module

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.CompilerModuleExtension
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.ocaml.sdk.OCamlSdkType
import java.io.File

open class BaseOCamlModuleBuilder : ModuleBuilder() {
    override fun isSuitableSdkType(sdkType: SdkTypeId?): Boolean = sdkType is OCamlSdkType
    override fun getModuleType(): ModuleType<*> = OCamlIdeaModuleType

    // The templates are not handled here anymore
    // The code of this method was heavily aligned with what is done with JAVA
    // While in practice the previous code was not much different
    fun setupRootModel(modifiableRootModel: ModifiableRootModel, sourcePaths: List<Pair<String?, String>>?) {
        super.setupRootModel(modifiableRootModel)

        // output folder
        val compilerModuleExtension = modifiableRootModel.getModuleExtension(
            CompilerModuleExtension::class.java
        )
        compilerModuleExtension.isExcludeOutput = true
        compilerModuleExtension.inheritCompilerOutputPath(true)

        // set the SDK "JDK"
        if (myJdk != null) modifiableRootModel.sdk = myJdk
        else modifiableRootModel.inheritSdk()

        val contentEntry = doAddContentEntry(modifiableRootModel)
        if (contentEntry != null) {
            val sourcePaths: List<Pair<String?, String>>? = sourcePaths
            if (sourcePaths != null) {
                for (sourcePath in sourcePaths) {
                    val first = sourcePath.first
                    File(first).mkdirs()
                    val sourceRoot = LocalFileSystem.getInstance()
                        .refreshAndFindFileByPath(FileUtil.toSystemIndependentName(first!!))
                    if (sourceRoot != null) {
                        contentEntry.addSourceFolder(sourceRoot, false, sourcePath.second!!)
                    }
                }
            }
        }
    }
}