package com.ocaml.language.psi.stubs.index

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndexKey
import com.ocaml.language.psi.api.OCamlNamedElement

class OCamlTypesFQNIndex : OCamlBaseFQNIndex<OCamlNamedElement>(Constants.KEY) {
    object Utils : OCamlBaseIndexUtils<OCamlNamedElement>(Constants.KEY) {
        fun findElementsByName(
            project: Project,
            target: String,
            scope: GlobalSearchScope = GlobalSearchScope.allScope(project),
        ): Collection<OCamlNamedElement> {
            return findElementsByName(Constants.KEY, project, target, scope)
        }
    }

    private object Constants {
        val KEY: StubIndexKey<Int, OCamlNamedElement> =
            StubIndexKey.createIndexKey("com.ocaml.index.OCamlTypesFQNIndex")
    }
}