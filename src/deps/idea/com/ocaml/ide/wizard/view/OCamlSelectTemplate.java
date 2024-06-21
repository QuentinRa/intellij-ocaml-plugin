/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * See the class documentation for changes.
 */
package com.ocaml.ide.wizard.view;

import com.intellij.ide.projectWizard.ProjectTemplateList;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectBuilder;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.platform.ProjectTemplate;
import com.intellij.ui.components.JBCheckBox;
import com.ocaml.OCamlBundle;
import com.ocaml.ide.wizard.OCamlModuleBuilder;
import com.ocaml.utils.adaptor.RequireJavaPlugin;

import javax.swing.*;
import java.util.ArrayList;

/**
 * The same interface used in ChooseTemplateStep, but we are using it
 * with different classes in the constructor/etc. as we are loading some
 * "unusual" templates.
 *
 * @see com.intellij.ide.projectWizard.ChooseTemplateStep
 */
@RequireJavaPlugin(what = "ProjectTemplateList -> need UI file so can't be replaced")
public class OCamlSelectTemplate extends ModuleWizardStep {
    private final WizardContext myWizardContext;
    private final ArrayList<ProjectTemplate> myAvailableTemplates;

    private JPanel myPanel;
    private JBCheckBox myCreateFromTemplateCheckBox;
    private ProjectTemplateList myTemplateList;

    public OCamlSelectTemplate(WizardContext wizardContext, ArrayList<ProjectTemplate> availableTemplates) {
        myWizardContext = wizardContext;
        myAvailableTemplates = availableTemplates;
        myCreateFromTemplateCheckBox.addActionListener(e -> {
            myTemplateList.setEnabled(myCreateFromTemplateCheckBox.isSelected());
            if (myCreateFromTemplateCheckBox.isSelected()) {
                IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(() -> IdeFocusManager.getGlobalInstance().requestFocus(myTemplateList.getList(), true));
            }
        });
        myTemplateList.setEnabled(false);
        // show "project" or "module" according to what we are creating
        myCreateFromTemplateCheckBox.setText(OCamlBundle.message(
                        "project.wizard.create.from.template",
                        wizardContext.isCreatingNewProject() ? 0 : 1
                )
        );
    }

    @Override
    public boolean isStepVisible() {
        return !myAvailableTemplates.isEmpty();
    }

    @Override
    public JComponent getComponent() {
        return myPanel;
    }

    @Override
    public void updateStep() {
        myTemplateList.setTemplates(myAvailableTemplates, false);
    }

    @Override
    public void updateDataModel() {
        ProjectBuilder projectBuilder = myWizardContext.getProjectBuilder();
        if (projectBuilder instanceof OCamlModuleBuilder)
            ((OCamlModuleBuilder) projectBuilder).setProjectTemplate(myCreateFromTemplateCheckBox.isSelected() ? myTemplateList.getSelectedTemplate() : null);
    }
}
