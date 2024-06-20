package com.ocaml.ide.module

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.projectRoots.SdkTypeId
import com.ocaml.sdk.OCamlSdkType

open class BaseOCamlModuleBuilder : ModuleBuilder() {
    override fun isSuitableSdkType(sdkType: SdkTypeId?): Boolean = sdkType is OCamlSdkType
    override fun getModuleType(): ModuleType<*> = OCamlIdeaModuleType
}