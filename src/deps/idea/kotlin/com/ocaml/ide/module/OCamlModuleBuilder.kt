package com.ocaml.ide.module

import com.intellij.openapi.roots.CompilerModuleExtension
import com.intellij.openapi.roots.ModifiableRootModel

class OCamlModuleBuilder : BaseOCamlModuleBuilder() {
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

        setupRootModel(rootModel, null)
    }
}
