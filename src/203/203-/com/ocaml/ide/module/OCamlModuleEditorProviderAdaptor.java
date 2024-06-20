package com.ocaml.ide.module;

import com.intellij.openapi.module.GeneralModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The code in this method changed in 211, but it was easy to create a patch
 * (ex: adding the new class in 211). Unfortunately, in 203, the method to get the*
 * ModifiableRootModel does not exist, so that why we need this class.
 */
public class OCamlModuleEditorProviderAdaptor {

    public static @Nullable Module getModuleFromState(@NotNull ModuleConfigurationState state) {
        Module module = state.getCurrentRootModel().getModule();
        ModuleType<?> moduleType = ModuleType.get(module);

        // We are not handling non-ocaml modules
        if (!(moduleType instanceof OCamlModuleType) &&
                (!GeneralModuleType.INSTANCE.equals(moduleType) || ProjectRootManager.getInstance(state.getProject()).getProjectSdk() == null)) {
            return null;
        }

        return module;
    }

}
