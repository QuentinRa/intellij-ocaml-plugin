package com.ocaml.ide.module

import com.intellij.ide.util.projectWizard.SourcePathsBuilder
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.util.Pair
import com.ocaml.ide.module.wizard.templates.TemplateBuildInstructions
import org.jetbrains.annotations.NonNls
import java.io.File

// JavaModuleBuilder.java
class OCamlModuleBuilder : BaseOCamlModuleBuilder(), SourcePathsBuilder {
    private var mySourcePaths: MutableList<Pair<String?, String>>? = null

    override fun getSourcePaths(): MutableList<Pair<String?, String>>? {
        if (mySourcePaths == null) {
            val paths: MutableList<Pair<String?, String>> = ArrayList()
            val path: @NonNls String = contentEntryPath + File.separator + TemplateBuildInstructions.sourceFolderName
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
        setupRootModel(rootModel, sourcePaths)
    }

    override fun isAvailable(): Boolean = false
}
