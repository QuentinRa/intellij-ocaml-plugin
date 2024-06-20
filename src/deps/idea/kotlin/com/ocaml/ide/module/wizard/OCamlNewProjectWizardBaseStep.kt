package com.ocaml.ide.module.wizard

import com.intellij.ide.projectWizard.ProjectWizardJdkIntent.ExistingJdk
import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.COLUMNS_LARGE
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.columns
import com.ocaml.OCamlBundle.message
import com.ocaml.ide.module.wizard.ui.OCamlProjectWizardJdkComboBox

open class OCamlNewProjectWizardBaseStep(parent: OCamlNewProjectWizard.OCamlNewProjectWizardStep) : AbstractNewProjectWizardStep(parent) {
    private val sdkProperty = propertyGraph.property<Sdk?>(null)
    private var sdk by sdkProperty

    override fun setupUI(builder: Panel) {
        super.setupUI(builder)

        builder.row(message("project.wizard.ocaml.sdk")) {
            val combo = OCamlProjectWizardJdkComboBox(context.projectJdk, context.disposable)
            cell(combo)
                .columns(COLUMNS_LARGE)
                .apply {
                    val commentCell = comment(component.comment, 50)
                    component.addItemListener {
                        commentCell.comment?.let { it.text = component.comment }
                    }
                }
                .onChanged {
                    val sdk = when (val intent = combo.selectedItem) {
                        is ExistingJdk -> intent.jdk
                        else -> null
                    }
                    sdkProperty.set(sdk)
                }
        }.bottomGap(BottomGap.SMALL)
    }
}