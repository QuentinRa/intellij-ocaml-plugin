package com.ocaml.language.psi.stubs.index

import com.intellij.psi.PsiElement
import com.intellij.psi.StubBuilder
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubIndexKey
import com.ocaml.ide.OCamlBasePlatformTestCase

abstract class BaseIndexTestCase<T> : OCamlBasePlatformTestCase() where T : PsiElement {
    interface StubIndexForTests<Key> {
        /**
         * Number of element indexed
         */
        var total : Int
        /**
         * Each name is mapped to its number of occurrences
         */
        val namedIndexValuesCount : MutableMap<Key, Int>

        fun count(key: String): Int?
    }


    private abstract class BaseFakeStubSinkIndex<Key, T> : IndexSink, StubIndexForTests<Key> where T : PsiElement {
        /**
         * Number of element indexed
         */
        override var total : Int = 0

        /**
         * Each name is mapped to its number of occurrences
         */
        override val namedIndexValuesCount : MutableMap<Key, Int> = HashMap()

        abstract fun isValid(namedIndex: Any): Boolean

        @Suppress("UNCHECKED_CAST", "UNUSED_VARIABLE")
        override fun <Psi : PsiElement?, K : Any?> occurrence(indexKey: StubIndexKey<K, Psi>, value: K & Any) {
            val namedIndex = indexKey as? StubIndexKey<Key, T> ?: return
            val indexValue = value as? Key ?: return
            if (!isValid(value)) return
            val count = namedIndexValuesCount[indexValue] ?: 0
            namedIndexValuesCount[indexValue] = count +1
            total++
        }
    }

    private class FakeFQNStubSinkIndex<T> : BaseFakeStubSinkIndex<Int, T>() where T : PsiElement {
        override fun count(key: String): Int?  {
            val hashCode = key.hashCode()
            return namedIndexValuesCount[hashCode]
        }

        override fun isValid(namedIndex: Any): Boolean {
            return namedIndex is Int
        }
    }

    private class FakeStubSinkIndex<T> : BaseFakeStubSinkIndex<String, T>() where T : PsiElement {
        override fun count(key: String): Int?  {
            return namedIndexValuesCount[key]
        }

        override fun isValid(namedIndex: Any): Boolean {
            return namedIndex is String
        }
    }

    /**
     * For instance, OCamlFileStub.Type.builder
     */
    abstract val builder: StubBuilder

    protected fun testFQNIndex(filename: String, code: String) : StubIndexForTests<Int> =
        process(FakeFQNStubSinkIndex(), filename, code)

    protected fun testIndex(filename: String, code: String) : StubIndexForTests<String> =
        process(FakeStubSinkIndex(), filename, code)

    private fun <Key> process(indexSink:  BaseFakeStubSinkIndex<Key, T>, filename: String, code: String) : StubIndexForTests<Key> {
        val file = configureCode(filename, code)
        val stubTree = builder.buildStubTree(file)
        stubTree.childrenStubs.forEach { it.stubType.indexStub(it, indexSink) }
        return indexSink
    }
}