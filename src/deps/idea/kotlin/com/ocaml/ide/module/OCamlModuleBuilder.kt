package com.ocaml.ide.module

import com.intellij.openapi.roots.CompilerModuleExtension
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.platform.ProjectTemplate

class OCamlModuleBuilder : BaseOCamlModuleBuilder() {
    private var myTemplate: ProjectTemplate? = null

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
}
