package com.ocaml.language.psi.stubs

import com.intellij.psi.StubBuilder
import com.intellij.psi.stubs.StubElement
import com.ocaml.ide.OCamlBasePlatformTestCase

abstract class BaseStubTestCase : OCamlBasePlatformTestCase() {
    /**
     * For instance, OCamlFileStub.Type.builder
     */
    abstract val builder: StubBuilder

    private fun <T: StubElement<*>> generateStubTree(filename: String, code: String, expectedChildren: Int): List<T> {
        val file = configureCode(filename, code)
        val stubTree = builder.buildStubTree(file)
        assertSize(expectedChildren, stubTree.childrenStubs)
        @Suppress("UNCHECKED_CAST")
        return stubTree.childrenStubs as List<T>
    }

    protected fun <T: StubElement<*>> generateOCamlStubTree(code: String, expectedChildren: Int): List<T> {
        return generateStubTree("A.ml", code, expectedChildren)
    }

    protected fun <T: StubElement<*>> generateOCamlInterfaceStubTree(code: String, expectedChildren: Int): List<T> {
        return generateStubTree("A.mli", code, expectedChildren)
    }
}