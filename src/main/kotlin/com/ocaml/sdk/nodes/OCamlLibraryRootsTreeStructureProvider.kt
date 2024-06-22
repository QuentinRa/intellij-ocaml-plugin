package com.ocaml.sdk.nodes

import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.psi.PsiFile
import com.ocaml.ide.files.OCamlFileType
import com.ocaml.ide.files.OCamlInterfaceFileType
import com.ocaml.sdk.utils.OCamlSdkRootsUtils
import java.util.function.Predicate

/**
 * Hides files that are not ending with either .ml or .mli
 */
class OCamlLibraryRootsTreeStructureProvider : TreeStructureProvider {
    override fun modify(
        parent: AbstractTreeNode<*>,
        children: Collection<AbstractTreeNode<*>>,
        settings: ViewSettings
    ): Collection<AbstractTreeNode<*>> {
        if (parent !is PsiDirectoryNode) return children
        val t = parent.parent
        if (!isLibraryRootForOCamlSdkRecursive(t)) return children
        // we are now sure that children is a list of the folders/files in the SDK
        // we need to remove every file that isn't
        // - ending with .ml
        // - ending with .mli
        val filtered: MutableList<AbstractTreeNode<*>> = ArrayList()
        for (child in children) {
            // Directories allowed
            if (child !is PsiFileNode) {
                filtered.add(child)
                continue
            }
            // Filter files
            val psiFile = child.value as PsiFile
            val filename = psiFile.name
            if (ALLOWED_FILES.test(filename)) filtered.add(child)
        }
        return filtered
    }

    companion object {
        /**
         * This pattern is matching files ending with
         *
         *  * ending with .ml
         *  * ending with .mli
         *
         */
        private val ALLOWED_FILES = Predicate<String> { s: String ->
            s.endsWith(OCamlFileType.DOT_DEFAULT_EXTENSION) || s.endsWith(OCamlInterfaceFileType.DOT_DEFAULT_EXTENSION)
        }

        /**
         * The folder may be nested, but still inside the SDK
         */
        private fun isLibraryRootForOCamlSdkRecursive(current: AbstractTreeNode<*>?): Boolean {
            if (current == null) return false
            // check parent instead
            if (current is PsiDirectoryNode) return isLibraryRootForOCamlSdkRecursive(current.parent)
            return OCamlSdkRootsUtils.isLibraryRootForOCamlSdk(current)
        }
    }
}
