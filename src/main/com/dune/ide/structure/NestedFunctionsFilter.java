package com.dune.ide.structure;

import com.intellij.icons.AllIcons;
import com.intellij.ide.util.treeView.smartTree.ActionPresentation;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.openapi.util.NlsActions;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class NestedFunctionsFilter implements Filter {

    @Override public boolean isVisible(TreeElement treeNode) {
        if (treeNode instanceof DuneStructureViewElement) {
            DuneStructureViewElement viewElement = (DuneStructureViewElement) treeNode;
            return viewElement.getLevel() < 2;
        }
        return true;
    }

    @Override public boolean isReverted() {
        return true;
    }

    // todo: bundle
    @Override public @NotNull ActionPresentation getPresentation() {
        return new ActionPresentation() {
            @Override public @NotNull @NlsActions.ActionText String getText() {
                return "Show Nested Functions";
            }

            @Override public @NlsActions.ActionDescription String getDescription() {
                return "Show nested functions";
            }

            @Override public Icon getIcon() {
                return AllIcons.General.InspectionsEye;
            }
        };
    }

    @Override public @NotNull String getName() {
        return "ShowNestedFunctions";
    }
}
