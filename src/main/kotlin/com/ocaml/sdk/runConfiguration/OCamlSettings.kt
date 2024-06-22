package com.ocaml.sdk.runConfiguration

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

/**
 * Create a file in .idea called ocaml.xml.
 * This file is only created if the user defined values that are
 * different of the default ones.
 */
@Service(Service.Level.PROJECT)
@State(name = "OCamlSettings", storages = [Storage("ocaml.xml")])
class OCamlSettings : PersistentStateComponent<OCamlSettings?> {
    /**
     * For Non-IntelliJ compatibles editors, this is the name of the
     * outputFolder, relative to the project root.
     */
    var outputFolderName: String = "out/"

    override fun getState(): OCamlSettings? {
        return this
    }

    override fun loadState(state: OCamlSettings) {
        outputFolderName = state.outputFolderName
    }

    companion object {
        fun getInstance(project: Project): OCamlSettings {
            return project.getService(OCamlSettings::class.java)
        }
    }
}