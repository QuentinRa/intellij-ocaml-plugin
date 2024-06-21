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
package com.ocaml.ide.module

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleConfigurationEditor
import com.intellij.openapi.roots.ui.configuration.*
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import javax.swing.Box
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * The [DefaultModuleEditorsProvider] is only available for
 * JAVA. This is an implementation for OCaml. Basically, in the editor,
 * in "Project Structure" > Modules, you will be able to see some tabs when you
 * are clicking on an OCaml module. These tabs are configured here.
 */
class OCamlModuleEditorProvider : ModuleConfigurationEditorProvider {
    override fun createEditors(state: ModuleConfigurationState): Array<ModuleConfigurationEditor> {
        val module: Module = OCamlModuleEditorProviderAdaptor.getModuleFromState(state)
            ?: return ModuleConfigurationEditor.EMPTY

        // creating the tabs
        val editors = ArrayList<ModuleConfigurationEditor>()
        editors.add(ClasspathEditor(state))
        editors.add(OCamlContentEntriesEditor(module.name, state))
        editors.add(OCamlOutputEditor(state))

        return editors.toArray(ModuleConfigurationEditor.EMPTY)
    }

    /**
     * OutputEditor without Javadoc and annotations panels
     */
    private class OCamlOutputEditor(state: ModuleConfigurationState?) : OutputEditor(state) {
        override fun createComponentImpl(): JComponent {
            val panel: JPanel = super.createComponentImpl() as JPanel
            panel.getComponent(1).setVisible(false) // javadocPanel
            panel.getComponent(2).setVisible(false) // annotationsPanel
            // adding some glue to fill the empty space
            val gc: GridBagConstraints = GridBagConstraints(
                0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, JBUI.emptyInsets(),
                0, 0
            )
            panel.add(Box.createVerticalGlue(), gc)
            return panel // :pray:
        }
    }

    private class OCamlContentEntriesEditor(moduleName: String?, state: ModuleConfigurationState?) :
        ContentEntriesEditor(moduleName, state) {
        override fun addAdditionalSettingsToPanel(mainPanel: JPanel) {
            super.addAdditionalSettingsToPanel(mainPanel) // :( NPE if we don't call it
            // replace the component :)
            // glory to the hacky way
            mainPanel.add(JLabel(" "), BorderLayout.NORTH)
        }
    }
}
