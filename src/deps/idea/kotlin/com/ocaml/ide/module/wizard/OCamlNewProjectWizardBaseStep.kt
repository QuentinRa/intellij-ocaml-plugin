package com.ocaml.ide.module.wizard

import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.COLUMNS_LARGE
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.columns
import com.ocaml.OCamlBundle.message
import com.ocaml.ide.module.wizard.ui.OCamlProjectWizardJdkComboBox

open class OCamlNewProjectWizardBaseStep(parent: OCamlNewProjectWizard.OCamlNewProjectWizardStep) : AbstractNewProjectWizardStep(parent) {
    override fun setupUI(builder: Panel) {
        super.setupUI(builder)

        builder.row(message("project.wizard.ocaml.sdk")) {
            val combo = OCamlProjectWizardJdkComboBox(context.projectJdk, context.disposable)
            cell(combo)
                .columns(COLUMNS_LARGE)
        }.bottomGap(BottomGap.SMALL)
    }

    override fun setupProject(project: Project) {
        super.setupProject(project)
    }
}