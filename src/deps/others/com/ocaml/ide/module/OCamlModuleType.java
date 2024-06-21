package com.ocaml.ide.module;

import com.intellij.ide.util.projectWizard.EmptyModuleBuilder;
import com.intellij.openapi.module.ModuleType;
import com.ocaml.OCamlBundle;
import com.ocaml.icons.OCamlIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * THIS FILE IS NOT USED IN PRODUCTION.
 * That's only because I need it to compile the project in minor IDEs, because the real
 * one require the Java plugin (in production, the real one is not loaded, so there is
 * no problem).
 */
public class OCamlModuleType extends ModuleType<EmptyModuleBuilder> {

    private static final String OCAML_MODULE = "OCAML_MODULE";

    public OCamlModuleType() {
        super(OCAML_MODULE);
    }

    @NotNull
    @Override
    public EmptyModuleBuilder createModuleBuilder() {
        return new EmptyModuleBuilder();
    }

    @Override
    public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getName() {
        return OCamlBundle.message("ocaml.module");
    }

    @Override
    public @NotNull @Nls(capitalization = Nls.Capitalization.Sentence) String getDescription() {
        return OCamlBundle.message("ocaml.module.description");
    }

    @Override
    public @NotNull Icon getNodeIcon(boolean isOpened) {
        return OCamlIcons.Nodes.OCAML_MODULE;
    }
}