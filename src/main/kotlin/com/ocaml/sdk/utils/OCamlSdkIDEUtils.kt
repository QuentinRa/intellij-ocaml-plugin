package com.ocaml.sdk.utils

import com.intellij.openapi.compiler.CompilerManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.CompilerModuleExtension
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.ocaml.OCamlBundle.message
import com.ocaml.sdk.OCamlSdkType
import com.ocaml.sdk.runConfiguration.OCamlSettings
import java.util.function.Supplier

object OCamlSdkIDEUtils {

    // find an SDK that can be used to start the REPL
    fun getSdk(project: Project): Sdk {
        var sdk = ProjectRootManager.getInstance(project).projectSdk
        if (sdk == null || sdk.sdkType !is OCamlSdkType) {
            for (module in ModuleManager.getInstance(project).modules) {
                val sdkCandidate = ModuleRootManager.getInstance(module).sdk
                if (sdkCandidate == null || sdkCandidate.sdkType !is OCamlSdkType) continue
                sdk = sdkCandidate
                break
            }
        }
        check(!(sdk == null || sdk.sdkType !is OCamlSdkType)) { message("repl.no.sdk") }
        return sdk
    }

    fun getModuleSdk(module: Module?): Sdk? {
        if (module != null && !module.isDisposed) {
            val sdk = ModuleRootManager.getInstance(module).sdk
            if (sdk != null && sdk.sdkType is OCamlSdkType) return sdk
        }
        return null
    }

    /** Get the SDK for the module which contains this file  */
    fun getModuleSdkForFile(project: Project, file: VirtualFile): Sdk? {
        return getModuleSdk(ModuleUtilCore.findModuleForFile(file, project))
    }

    fun isInProject(project: Project, file: VirtualFile): Boolean {
        // not excluded, nor ignored, and in a source folder
        return ProjectFileIndex.getInstance(project).isInContent(file)
    }

    /** is not in a folder marked as "excluded"  */
    fun isNotExcluded(project: Project, virtualFile: VirtualFile): Boolean {
        if (isJavaPluginAvailable()) return !CompilerManager.getInstance(project)
            .isExcludedFromCompilation(virtualFile)
        // if not defined, check if we are in the project
        return isInProject(project, virtualFile)
    }

    private var isJavaAvailable: Boolean? = null
    private fun isJavaPluginAvailable(): Boolean {
        if (isJavaAvailable == null) {
            try {
                Class.forName("com.intellij.ide.highlighter.JavaFileType")
                isJavaAvailable = true
            } catch (e: ClassNotFoundException) {
                isJavaAvailable = false
            }
        }
        return isJavaAvailable!!
    }

    fun findOutputFolder(module: Module, project: Project): String {
        return findOutputFolder(ModuleRootManager.getInstance(module), project)
    }

    private fun findOutputFolder(moduleRootManager: ModuleRootManager, project: Project): String {
        return findOutputFolder(moduleRootManager, project) { project.basePath }
    }

    private fun findOutputFolder(
        moduleRootManager: ModuleRootManager,
        project: Project,
        rootFolder: Supplier<String?>
    ): String {
        if (isJavaPluginAvailable()) {
            // output folder
            val compilerModuleExtension = moduleRootManager.getModuleExtension(
                CompilerModuleExtension::class.java
            )
            val outputPointer = compilerModuleExtension.compilerOutputPointer
            return outputPointer.presentableUrl + "/"
        } else {
            // get outputFolder
            val outputFolderName: String = project.getService(OCamlSettings::class.java).outputFolderName
            val basePath = rootFolder.get()
            return "$basePath/$outputFolderName"
        }
    }
}