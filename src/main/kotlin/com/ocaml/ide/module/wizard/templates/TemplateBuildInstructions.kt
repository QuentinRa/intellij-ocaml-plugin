package com.ocaml.ide.module.wizard.templates

import com.intellij.openapi.vfs.VirtualFile


/**
 * Instructions to build the template.
 */
interface TemplateBuildInstructions {
    /**
     * Create files used by the template

     * @param sourceRoot the folder that was created using [.getSourceFolderName]
     * @see com.intellij.ide.util.projectWizard.ModuleBuilder.setupRootModel
     */
    fun createFiles(sourceRoot: VirtualFile?)

    fun createFiles(sourceRoot: VirtualFile?, sdkHomePath: String?) {
        createFiles(sourceRoot)
    }

    companion object {
        /**
         * Name of the source folder that will be created.
         */
        const val sourceFolderName = "src"
    }
}