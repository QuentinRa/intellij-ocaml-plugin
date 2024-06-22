package com.ocaml.sdk.nodes

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.ocaml.icons.OCamlIcons
import com.ocaml.sdk.utils.OCamlSdkRootsUtils

/**
 * Add an icon to the nodes in "External libraries" that
 * are inside an OCaml SDK.
 */
class OCamlLibraryRootsNodeDecorator : ProjectViewNodeDecorator {
    override fun decorate(node: ProjectViewNode<*>, data: PresentationData) {
        if (node !is PsiDirectoryNode) return

        // get library node
        val parentDescriptor = node.getParentDescriptor()
        if (!OCamlSdkRootsUtils.isLibraryRootForOCamlSdk(parentDescriptor)) return

        // set icon
        data.setIcon(OCamlIcons.Nodes.OCAML_LIBRARY)
    }
}
