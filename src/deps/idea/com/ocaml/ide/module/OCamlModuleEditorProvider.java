/*
 * Copyright 2000-2009 JetBrains s.r.o.
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
package com.ocaml.ide.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.roots.ui.configuration.*;
import com.intellij.util.ui.JBUI;
import com.ocaml.utils.adaptor.RequireJavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link DefaultModuleEditorsProvider} is only available for
 * JAVA. This is an implementation for OCaml. Basically, in the editor,
 * in "Project Structure" > Modules, you will be able to see some tabs when you
 * are clicking on an OCaml module. These tabs are configured here.
 */
@RequireJavaPlugin
public class OCamlModuleEditorProvider implements ModuleConfigurationEditorProvider {

    @Override
    public ModuleConfigurationEditor[] createEditors(ModuleConfigurationState state) {
        Module module = OCamlModuleEditorProviderAdaptor.getModuleFromState(state);
        if (module == null)
            return ModuleConfigurationEditor.EMPTY;

        // creating the tabs
        List<ModuleConfigurationEditor> editors = new ArrayList<>();
        editors.add(new ClasspathEditor(state));
        editors.add(new OCamlContentEntriesEditor(module.getName(), state));
        editors.add(new OCamlOutputEditor(state));

        return editors.toArray(ModuleConfigurationEditor.EMPTY);
    }

    /**
     * OutputEditor without Javadoc and annotations panels
     */
    private static final class OCamlOutputEditor extends OutputEditor {

        public OCamlOutputEditor(ModuleConfigurationState state) {
            super(state);
        }

        @Override
        protected @NotNull JComponent createComponentImpl() {
            JPanel panel = (JPanel) super.createComponentImpl();
            panel.getComponent(1).setVisible(false); // javadocPanel
            panel.getComponent(2).setVisible(false); // annotationsPanel
            // adding some glue to fill the empty space
            final GridBagConstraints gc = new GridBagConstraints(
                    0, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
                    GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, JBUI.emptyInsets(),
                    0, 0
            );
            panel.add(Box.createVerticalGlue(), gc);
            return panel; // :pray:
        }
    }

    private static final class OCamlContentEntriesEditor extends ContentEntriesEditor {

        public OCamlContentEntriesEditor(String moduleName, ModuleConfigurationState state) {
            super(moduleName, state);
        }

        @Override
        protected void addAdditionalSettingsToPanel(JPanel mainPanel) {
            super.addAdditionalSettingsToPanel(mainPanel); // :( NPE if we don't call it
            // replace the component :)
            // glory to the hacky way
            mainPanel.add(new JLabel(" "), BorderLayout.NORTH);
        }
    }
}
