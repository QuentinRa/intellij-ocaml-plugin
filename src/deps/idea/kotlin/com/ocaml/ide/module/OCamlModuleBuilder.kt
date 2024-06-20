package com.ocaml.ide.module

import com.intellij.ide.util.projectWizard.SourcePathsBuilder
import com.intellij.openapi.roots.CompilerModuleExtension
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import org.jetbrains.annotations.NonNls
import java.io.File

// JavaModuleBuilder.java
class OCamlModuleBuilder : BaseOCamlModuleBuilder(), SourcePathsBuilder {
    private var mySourcePaths: MutableList<Pair<String?, String>>? = null

    override fun getSourcePaths(): MutableList<Pair<String?, String>>? {
        if (mySourcePaths == null) {
            val paths: MutableList<Pair<String?, String>> = ArrayList()
            val path: @NonNls String = contentEntryPath + File.separator + "src"
            File(path).mkdirs()
            paths.add(Pair.create(path, ""))
            return paths
        }
        return mySourcePaths
    }
    override fun setSourcePaths(sourcePaths: MutableList<Pair<String?, String>>?) { mySourcePaths = sourcePaths }
    override fun addSourcePath(sourcePathInfo: Pair<String?, String>?) {
        if (mySourcePaths == null) mySourcePaths = ArrayList()
        if (sourcePathInfo != null) mySourcePaths!!.add(sourcePathInfo)
    }

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

        val contentEntry = doAddContentEntry(rootModel)
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

    override fun isAvailable(): Boolean = false
}
