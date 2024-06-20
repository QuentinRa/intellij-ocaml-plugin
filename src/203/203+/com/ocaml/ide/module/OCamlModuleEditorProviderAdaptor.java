package com.ocaml.ide.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The check in this method changed in 211 (included), but we can't handle this fine.
 * Unfortunately, in 203, the method to get the ModifiableRootModel does not exists, so
 * that why we need this class.
 */
public class OCamlModuleEditorProviderAdaptor {

    public static @Nullable Module getModuleFromState(@NotNull ModuleConfigurationState state) {
        ModifiableRootModel rootModel = state.getRootModel();
        Module module = rootModel.getModule();
        if (!(ModuleType.get(module) instanceof OCamlModuleType)) {
            return null;
        }
        return module;
    }

}
