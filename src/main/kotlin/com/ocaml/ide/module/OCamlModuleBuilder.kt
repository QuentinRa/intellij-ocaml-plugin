package com.ocaml.ide.module

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ModifiableRootModel
import com.ocaml.sdk.OCamlSdkType

typealias StringSupplier = () -> String?

open class BaseOCamlModuleBuilder : ModuleBuilder() {
    private var contentEntryPath: StringSupplier? = null

    override fun isSuitableSdkType(sdkType: SdkTypeId?): Boolean = sdkType is OCamlSdkType
    override fun getModuleType(): ModuleType<*> = OCamlIdeaModuleType
    override fun getContentEntryPath(): String? = contentEntryPath!!()
    private val defaultContentEntryPath = { super.getContentEntryPath() }

    fun setupRootModel(
        rootModel: ModifiableRootModel,
        contentEntryPath: StringSupplier?,
    ) {
        this.contentEntryPath = contentEntryPath ?: defaultContentEntryPath
        doAddContentEntry(rootModel)
    }
}