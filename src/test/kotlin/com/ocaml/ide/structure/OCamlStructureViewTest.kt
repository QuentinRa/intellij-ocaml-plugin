package com.ocaml.ide.structure

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.ocaml.ide.OCamlBasePlatformTestCase
import org.junit.Test

class OCamlStructureViewTest : OCamlBasePlatformTestCase() {

    // OCamlBaseStructureTestCase
    private data class FakeTreeElement(val name: String, val children : List<FakeTreeElement> = listOf()) : TreeElement {
        override fun getPresentation(): ItemPresentation {
            return PresentationData(name, null, null, null)
        }

        override fun getChildren(): Array<TreeElement> = children.toTypedArray()
    }
    private fun configureStructureView(filename: String, code: String): Array<out TreeElement> {
        val a = configureCode(filename, code)
        val viewModel = OCamlStructureViewModel(myFixture.editor, a)
        return viewModel.root.children
    }
    private fun assertStructureTree(filename: String, code: String, vararg expectedTree : TreeElement) {
        val originalTree = configureStructureView(filename, code)
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
    // End

    @Test
    fun test_anonymous() {
        assertEmpty(configureStructureView("A.ml", "let _ = ()"))
        assertEmpty(configureStructureView("A.ml", "let () = ()"))
        assertEmpty(configureStructureView("A.ml", "let (_) = ()"))
        //Not possible: "val _ : unit"
    }

    @Test
    fun test_basic_declarations() {
        val expectedTree = FakeTreeElement("x")
        assertStructureTree("A.ml", "let x = ()", expectedTree)
        assertStructureTree("A.mli","val x : int", expectedTree)
    }

    @Test
    fun test_multiple() {
        val xElement = FakeTreeElement("x")
        val yElement = FakeTreeElement("y")
        val zElement = FakeTreeElement("z")
        // Test an OCaml Implementation
//        assertStructureTree("A.ml", """
//                let x = ()
//                let y = ()
//                let z = ()
//                """, xElement, yElement, zElement
//        )
        // Test an OCaml Interface
        assertStructureTree("A.mli", """
                val x : unit
                val y : unit
                val z : unit
                """, xElement, yElement, zElement
        )
    }

    @Test
    fun test_duplicates() {
        val xElement = FakeTreeElement("x")
        assertStructureTree("A.ml", "let x = ();;let x = ()", xElement, xElement)
        assertStructureTree("A.mli","val x : int;;val x: int", xElement, xElement)
    }

    @Test
    fun test_pattern_declarations() {
        assertStructureTree("A.ml",
            "let a,b = ()",
            FakeTreeElement("a"), FakeTreeElement("b")
        )

        assertStructureTree("A.ml",
            "let ((a,b),c) = ()",
            FakeTreeElement("a"), FakeTreeElement("b"), FakeTreeElement("c")
        )

        assertStructureTree("A.ml",
            "let (((+),(-)),_, (a)) = ()",
            FakeTreeElement("( + )"), FakeTreeElement("( - )"), FakeTreeElement("a")
        )
    }
}