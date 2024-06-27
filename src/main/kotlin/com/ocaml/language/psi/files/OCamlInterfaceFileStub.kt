package com.ocaml.language.psi.files

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiFile
import com.intellij.psi.StubBuilder
import com.intellij.psi.impl.source.tree.TreeUtil
import com.intellij.psi.stubs.DefaultStubBuilder
import com.intellij.psi.stubs.PsiFileStubImpl
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.IStubFileElementType
import com.ocaml.language.OCamlInterfaceLanguage
import com.ocaml.language.base.OCamlBaseParserDefinition
import com.ocaml.language.psi.OCamlTypes
import com.ocaml.language.psi.stubs.OCamlStubVersions

class OCamlInterfaceFileStub(file: OCamlInterfaceFile?) : PsiFileStubImpl<OCamlInterfaceFile>(file) {
    override fun getType() = Type
    object Type : IStubFileElementType<OCamlInterfaceFileStub>(OCamlInterfaceLanguage) {
        override fun getStubVersion(): Int = OCamlBaseParserDefinition.PARSER_VERSION + OCamlStubVersions.STUB_VERSION

        override fun getBuilder(): StubBuilder = object : DefaultStubBuilder() {
            override fun skipChildProcessingWhenBuildingStubs(parent: ASTNode, node: ASTNode) = when (parent.elementType) {
                OCamlTypes.TYPE_DEFINITION -> false // Parse direct children of TYPE
                OCamlTypes.VALUE_DESCRIPTION -> false // Parse direct children of VAL
                is IFileElementType -> false // Parse direct children of PsiFile
                else -> true // Skip everything else
            }

            override fun createStubForFile(file: PsiFile): StubElement<*> {
                TreeUtil.ensureParsed(file.node) // profiler hint
                check(file is OCamlInterfaceFile)
                return OCamlInterfaceFileStub(file)
            }
        }

        override fun getExternalId(): String = "ocaml.file.mli"
    }
}