package com.ocaml.sdk.utils

import com.intellij.ide.projectView.impl.nodes.NamedLibraryElementNode
import com.intellij.ide.util.treeView.NodeDescriptor
import com.intellij.openapi.roots.JdkOrderEntry
import com.intellij.openapi.roots.LibraryOrSdkOrderEntry
import com.ocaml.sdk.OCamlSdkType
import com.ocaml.sdk.providers.OCamlSdkProvidersManager.getAssociatedSourcesFolders

object OCamlSdkRootsUtils {
    /**
     * @see com.ocaml.sdk.providers.OCamlSdkProvider.getAssociatedSourcesFolders
     */
    fun getSourcesFolders(sdkHome: String): List<String> {
        return ArrayList(getAssociatedSourcesFolders(sdkHome))
    }

    fun isLibraryRootForOCamlSdk(parent: NodeDescriptor<*>?): Boolean {
        if (parent !is NamedLibraryElementNode) return false
        val library = parent.value ?: return false

        // check SDK
        val orderEntry: LibraryOrSdkOrderEntry = library.orderEntry as? JdkOrderEntry ?: return false
        val sdk = (orderEntry as JdkOrderEntry).jdk ?: return false

        // final ~~~boss~~ check
        return sdk.sdkType is OCamlSdkType
    }
}