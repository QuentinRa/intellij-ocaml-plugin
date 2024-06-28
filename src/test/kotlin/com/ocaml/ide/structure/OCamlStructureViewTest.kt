package com.ocaml.ide.structure

import com.BaseStructureTestCase
import com.BaseStructureTestCase.FakeTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.ocaml.ide.OCamlBasePlatformTestCase
import org.junit.Test

class OCamlStructureViewTest : OCamlBasePlatformTestCase() {

    // OCamlBaseStructureTestCase
    private fun configureStructureView(filename: String, code: String): Array<out TreeElement> {
        val a = configureCode(filename, code)
        val viewModel = OCamlStructureViewModel(myFixture.editor, a)
        return viewModel.root.children
    }
    private fun assertStructureTree(filename: String, code: String, vararg expectedTree : TreeElement) {
        BaseStructureTestCase.assertStructureTree(configureStructureView(filename, code), expectedTree)
    }
    // End

    @Test
    fun test_anonymous() {
        assertEmpty(configureStructureView("A.ml", "let _ = ()"))
        assertEmpty(configureStructureView("A.ml", "let () = ()"))
        assertEmpty(configureStructureView("A.ml", "let (_) = ()"))
        assertEmpty(configureStructureView("A.ml", "let _ = x Hello_world.x"))
        //Not possible: "type _ = unit"
        //Not possible: "val _ : unit"
    }

    @Test
    fun test_basic_declarations() {
        val expectedTree = FakeTreeElement("x")
        assertStructureTree("A.ml", "let x = ()", expectedTree)
        assertStructureTree("A.mli","val x : int", expectedTree)
        assertStructureTree("A.ml","type x = unit", expectedTree)
    }

    @Test
    fun test_multiple() {
        val xElement = FakeTreeElement("x")
        val yElement = FakeTreeElement("y")
        val zElement = FakeTreeElement("z")
        // Test an OCaml Implementation
        assertStructureTree("A.ml", """
                let x = ()
                let y = ()
                let z = ()
                """, xElement, yElement, zElement
        )
        // Test an OCaml Interface
        assertStructureTree("A.mli", """
                val x : unit
                val y : unit
                val z : unit
                """, xElement, yElement, zElement
        )
        // Test types
        assertStructureTree("A.ml", """
                type x = unit
                type y = unit
                type z = unit
                """, xElement, yElement, zElement
        )
    }

    @Test
    fun test_duplicates() {
        val xElement = FakeTreeElement("x")
        assertStructureTree("A.ml", "let x = ();;let x = ()", xElement, xElement)
        assertStructureTree("A.mli","val x : int;;val x: int", xElement, xElement)
        assertStructureTree("A.ml","type x = unit;;type x = unit", xElement, xElement)
    }

    @Test
    fun test_and() {
        assertStructureTree("A.ml", "let x = () and y = ()", FakeTreeElement("let x, y",
            listOf(
                FakeTreeElement("x"),
                FakeTreeElement("y"),
            )
        ))
        assertStructureTree("A.ml","type x and y = unit", FakeTreeElement("type x, y",
            listOf(
                FakeTreeElement("x"),
                FakeTreeElement("y"),
            )
        ))
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

    @Test
    fun test_pattern_declarations_fix() {
        assertStructureTree("A.ml",
            "let (a, b) = c",
            FakeTreeElement("a"), FakeTreeElement("b")
        )
    }

    @Test
    fun test_nested() {
        assertStructureTree("A.ml",
            "let a = let b = 5 in let c = 6 in b * c",
            FakeTreeElement("a", listOf(
                FakeTreeElement("b"),
                FakeTreeElement("c"),
            ))
        )
        assertStructureTree("A.ml",
            "let a = let (b, c) = 5,6 in b * c",
            FakeTreeElement("a", listOf(
                FakeTreeElement("b"),
                FakeTreeElement("c"),
            ))
        )

        assertStructureTree("A.ml",
            "let a = let b = 5,6 in let (c, d) = b in c * d",
            FakeTreeElement("a", listOf(
                FakeTreeElement("b"),
                FakeTreeElement("c, d"),
            ))
        )
    }
}