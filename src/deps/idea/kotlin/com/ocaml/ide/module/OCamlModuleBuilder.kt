package com.ocaml.ide.module

import com.intellij.openapi.roots.CompilerModuleExtension
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.platform.ProjectTemplate

class OCamlModuleBuilder : BaseOCamlModuleBuilder() {
    private var myTemplate: ProjectTemplate? = null

    //    override fun isSuitableSdkType(sdkType: SdkTypeId?): Boolean = sdkType is OCamlSdkType
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

    // The options' step is the first step, the user will select an SDK
//    override fun getCustomOptionsStep(context: WizardContext?, parentDisposable: Disposable?): ModuleWizardStep {
//        return OCamlSdkWizardStep(context, this)
//    }

//    /**
//     * Show steps after the custom option steps
//     * and before the project step
//     */
//    fun createWizardSteps(wizardContext: WizardContext?, modulesProvider: ModulesProvider?): Array<ModuleWizardStep> {
//        return arrayOf(OCamlSelectTemplate(wizardContext, OCamlTemplateProvider.availableTemplates))
//    }
}