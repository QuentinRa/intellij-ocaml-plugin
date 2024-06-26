package com.ocaml.language.psi.stubs.index

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.PsiDocumentManagerBase
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.*
import com.ocaml.language.psi.api.OCamlNamedElement
import com.ocaml.language.psi.files.OCamlFileStub

// An attempt to reuse code
abstract class OCamlBaseIndex<T>(private val key: StubIndexKey<String, T>) :  StringStubIndexExtension<T>() where T : PsiElement? {
    override fun getVersion(): Int = OCamlFileStub.Type.stubVersion
    override fun getKey(): StubIndexKey<String, T> = key

    open class OCamlBaseIndexUtils<T>(private val indexKey: StubIndexKey<String, T>) where T : PsiElement {
        fun index(sink: IndexSink, key: String) {
            sink.occurrence(indexKey, key)
        }

        inline fun <reified T : PsiElement> findElementsByName(
            indexKey: StubIndexKey<String, T>,
            target: String,
            project: Project,
            scope: GlobalSearchScope
        ): Collection<T> {
            return StubIndex.getElements(indexKey, target, project, scope, T::class.java)
        }
    }
}

class OCamlVariablesIndex : OCamlBaseIndex<OCamlNamedElement>(Constants.KEY) {
    object Utils : OCamlBaseIndexUtils<OCamlNamedElement>(Constants.KEY)

    private object Constants {
        val KEY: StubIndexKey<String, OCamlNamedElement> =
            StubIndexKey.createIndexKey("com.ocaml.index.OCamlNamedElementIndex")
    }
}

fun checkCommitIsNotInProgress(project: Project) {
    val app = ApplicationManager.getApplication()
    if ((app.isUnitTestMode || app.isInternal) && app.isDispatchThread) {
        if ((PsiDocumentManager.getInstance(project) as PsiDocumentManagerBase).isCommitInProgress) {
            error("Accessing indices during PSI event processing can lead to typing performance issues")
        }
    }
}