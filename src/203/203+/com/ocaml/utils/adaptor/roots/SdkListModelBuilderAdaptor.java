package com.ocaml.utils.adaptor.roots;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ui.configuration.SdkListItem;
import com.intellij.openapi.roots.ui.configuration.SdkListModelBuilder;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.util.Condition;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SdkListModelBuilderAdaptor {

    @SuppressWarnings("unused") @Contract("_, _, _, _, _, _, _ -> new")
    public static @NotNull SdkListModelBuilder create(@Nullable Project project,
                                                      @NotNull ProjectSdksModel sdkModel,
                                                      @Nullable Condition<? super SdkTypeId> sdkTypeFilter,
                                                      @Nullable Condition<? super SdkTypeId> sdkTypeCreationFilter,
                                                      @Nullable Condition<? super Sdk> sdkFilter,
                                                      @Nullable Condition<? super SdkListItem.SuggestedItem> suggestedSdkFilter,
                                                      @Nullable Condition<? super SdkListItem.ActionRole> actionRoleFilter) {
        return new SdkListModelBuilder(project, sdkModel, sdkTypeFilter, sdkTypeCreationFilter, sdkFilter);
    }
}
