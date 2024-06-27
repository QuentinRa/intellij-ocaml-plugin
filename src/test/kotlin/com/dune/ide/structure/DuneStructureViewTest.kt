package com.dune.ide.structure

import com.BaseStructureTestCase
import com.BaseStructureTestCase.FakeTreeElement
import com.dune.ide.DuneBasePlatformTestCase
import com.intellij.ide.util.treeView.smartTree.TreeElement
import org.junit.Test

class DuneStructureViewTest : DuneBasePlatformTestCase() {

    private fun configureStructureView(code: String): Array<out TreeElement> {
        val a = configureCode("dune", code)
        val viewModel = DuneStructureViewModel(myFixture.editor, a)
        return viewModel.root.children
    }
    private fun assertStructureTree(code: String, vararg expectedTree : TreeElement) {
        BaseStructureTestCase.assertStructureTree(configureStructureView(code), expectedTree)
    }

    @Test
    fun test_empty_tree() {
        assertStructureTree("()")
    }

    @Test
    fun test_basic_tree() {
        assertStructureTree("(executable\n" +
                " (name my_executable)\n" +
                " (libraries core my_library))",
            FakeTreeElement("executable",
                listOf(
                    FakeTreeElement("name"),
                    FakeTreeElement("libraries"),
                )
            )
        )
    }
}