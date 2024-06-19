package com.ocaml.ide.module.wizard.templates

import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.vfs.VirtualFile


/**
 * Instructions to build the template.
 */
interface TemplateBuildInstructions {
    /**
     * Name of the source folder that will be created.
     */
    val sourceFolderName: String
        get() = "src"

    /**
     * Create files used by the template
     *
     * @param rootModel  see setupRootModel
     * @param sourceRoot the folder that was created using [.getSourceFolderName]
     * @see com.intellij.ide.util.projectWizard.ModuleBuilder.setupRootModel
     */
    fun createFiles(rootModel: ModifiableRootModel?, sourceRoot: VirtualFile?)

    fun createFiles(rootModel: ModifiableRootModel?, sourceRoot: VirtualFile?, sdkHomePath: String?) {
        createFiles(rootModel, sourceRoot)
    }
}