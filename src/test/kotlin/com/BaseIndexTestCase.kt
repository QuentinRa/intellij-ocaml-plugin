package com

import com.intellij.psi.PsiElement
import com.intellij.psi.StubBuilder
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubIndexKey
import com.ocaml.ide.OCamlBasePlatformTestCase
import com.ocaml.language.psi.api.OCamlNamedElement
import com.ocaml.language.psi.files.OCamlFileStub

interface StubIndexForTests {
    /**
     * Number of element indexed
     */
    var total : Int
    /**
     * Each name is mapped to its number of occurrences
     */
    val namedIndexValuesCount : MutableMap<String, Int>
}

abstract class BaseIndexTestCase<T> : OCamlBasePlatformTestCase() where T : PsiElement {

    private class FakeStubSinkIndex<T> : IndexSink, StubIndexForTests where T : PsiElement {
        /**
         * Number of element indexed
         */
        override var total : Int = 0

        /**
         * Each name is mapped to its number of occurrences
         */
        override val namedIndexValuesCount : MutableMap<String, Int> = HashMap()

        override fun <Psi : PsiElement?, K : Any?> occurrence(indexKey: StubIndexKey<K, Psi>, value: K & Any) {
            @Suppress("UNCHECKED_CAST", "UNUSED_VARIABLE")
            val namedIndex = indexKey as? StubIndexKey<String, T> ?: return
            val indexValue = value as? String ?: return
            println(indexValue)
            val count = namedIndexValuesCount[indexValue] ?: 0
            namedIndexValuesCount[indexValue] = count +1
            total++
        }
    }

    /**
     * For instance, OCamlFileStub.Type.builder
     */
    abstract val builder: StubBuilder

    protected fun testIndex(filename: String, code: String) : StubIndexForTests {
        val file = configureCode(filename, code)
        val stubTree = builder.buildStubTree(file)
        val indexSink = FakeStubSinkIndex<T>()
        stubTree.childrenStubs.forEach { it.stubType.indexStub(it, indexSink) }
        return indexSink
    }
}