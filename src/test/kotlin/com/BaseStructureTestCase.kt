package com

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.testFramework.fixtures.BasePlatformTestCase.assertTrue
import com.intellij.testFramework.fixtures.BasePlatformTestCase.fail

/**
 * Utilities to test if the structure view is the expected one
 */
object BaseStructureTestCase {

    data class FakeTreeElement(val name: String, val children : List<FakeTreeElement> = listOf()) :
        TreeElement {
        override fun getPresentation(): ItemPresentation {
            return PresentationData(name, null, null, null)
        }

        override fun getChildren(): Array<TreeElement> = children.toTypedArray()
    }

    fun assertStructureTree(originalTree: Array<out TreeElement>, expectedTree : Array<out TreeElement>) {
        // Ensure both tree have the same variables
        fun areTreeSimilar(treeA: Array<out TreeElement>, treeB: Array<out TreeElement>) : Boolean {
            return treeA.size == treeB.size && treeA.zip(treeB).all { (e1, e2) ->
                e1.presentation.presentableText == e2.presentation.presentableText &&
                        areTreeSimilar(e1.children, e2.children)
            }
        }
        // Only print arrays on failure
        val result =  areTreeSimilar(originalTree, expectedTree)
        if (!result) {
            fun inlineTreeAsString(tree: Array<out TreeElement>): String {
                return "[" + tree.joinToString { it.presentation.presentableText +
                        (if (it.children.isNotEmpty()) inlineTreeAsString(it.children) else "") } + "]"
            }
            fail("Expected: ${inlineTreeAsString(expectedTree)}, got: ${inlineTreeAsString(originalTree)}")
        } else {
            assertTrue(true)
        }
    }
}