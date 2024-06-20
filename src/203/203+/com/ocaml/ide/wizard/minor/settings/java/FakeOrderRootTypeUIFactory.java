// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.ocaml.ide.wizard.minor.settings.java;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.extensions.KeyedFactoryEPBean;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.ui.SdkPathEditor;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ui.OrderRootTypeUIFactory;
import com.intellij.openapi.util.KeyedExtensionFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author anna
 */
public interface FakeOrderRootTypeUIFactory extends OrderRootTypeUIFactory {
    ExtensionPointName<KeyedFactoryEPBean> EP_NAME = ExtensionPointName.create("com.intellij.FakeOrderRootTypeUI");
    KeyedExtensionFactory<FakeOrderRootTypeUIFactory, OrderRootType> FACTORY =
            new KeyedExtensionFactory<>(FakeOrderRootTypeUIFactory.class, EP_NAME, ApplicationManager.getApplication().getPicoContainer()) {
                @NotNull
                @Override
                public String getKey(@NotNull final OrderRootType key) {
                    return key.name();
                }
            };

    @Nullable
    SdkPathEditor createPathEditor(Sdk sdk);

    Icon getIcon();

    String getNodeText();
}