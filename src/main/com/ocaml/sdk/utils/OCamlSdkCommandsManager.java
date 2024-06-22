package com.ocaml.sdk.utils;

import com.esotericsoftware.minlog.Log;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.PtyCommandLine;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.ocaml.OCamlBundle;
import com.ocaml.sdk.OCamlSdkType;
import com.ocaml.sdk.providers.OCamlSdkProvidersManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

// note that this class will be removed later, when the REPL will be remade
@ApiStatus.Internal
public final class OCamlSdkCommandsManager {

    // returns the command to start the REPL
    public static @NotNull GeneralCommandLine getREPLCommand(@NotNull Project project) {
        Sdk sdk = getSdk(project);

        // get command
        GeneralCommandLine replCommand = OCamlSdkProvidersManager.INSTANCE.getREPLCommand(sdk.getHomePath());

        // should NOT be null, even if everyone delegates the creation, the default provider isn't
        if (replCommand == null)
            throw new IllegalStateException("Unable to start the console.");

        Log.debug("REPL command is:" + replCommand.getCommandLineString());

        // return PtyCommand
        return new PtyCommandLine(replCommand).withInitialColumns(PtyCommandLine.MAX_COLUMNS);
    }
}
