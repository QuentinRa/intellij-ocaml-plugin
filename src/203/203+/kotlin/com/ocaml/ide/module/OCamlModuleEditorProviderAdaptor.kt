package com.ocaml.ide.module

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState
import com.ocaml.language.psi.OCamlModuleType

/**
 * The check in this method changed in 211 (included), but we can't handle this fine.
 * Unfortunately, in 203, the method to get the ModifiableRootModel does not exists, so
 * that why we need this class.
 */
object OCamlModuleEditorProviderAdaptor {
    fun getModuleFromState(state: ModuleConfigurationState): Module? {
        val rootModel = state.currentRootModel
        val module = rootModel.module
        if (ModuleType.get(module) !is OCamlIdeaModuleType) {
            return null
        }
        return module
    }
}
