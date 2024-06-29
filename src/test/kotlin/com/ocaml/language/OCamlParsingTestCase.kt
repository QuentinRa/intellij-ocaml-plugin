package com.ocaml.language

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.lang.ParserDefinition
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.ParsingTestCase
import com.ocaml.language.parser.OCamlInterfaceParserDefinition
import com.ocaml.language.parser.OCamlParserDefinition
import com.ocaml.language.psi.OCamlImplUtils.toLeaf
import com.ocaml.language.psi.api.OCamlNameIdentifierOwner
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
abstract class OCamlBaseParsingTestCase(fileExt: String, parserDefinition: ParserDefinition) :
    ParsingTestCase("", fileExt, parserDefinition) {
    protected companion object {
        const val FILE_NAME = "dummy"
        const val OCAML_FILE_QUALIFIED_NAME = "Dummy"
        const val OCAML_FILE_QUALIFIED_NAME_DOT = "$OCAML_FILE_QUALIFIED_NAME."
    }

    override fun getTestDataPath(): String {
        return "resources/testData"
    }

    // Parse
    private fun parseRawCode(code: String): PsiFile {
        myFile = createPsiFile(FILE_NAME, code)
        println(com.intellij.psi.impl.DebugUtil.psiToString(myFile, false, true))
        return myFile
    }

    protected open fun parseCode(code: String): PsiFileBase {
        return parseRawCode(code) as PsiFileBase
    }

    // Doesn't work well with MLI wtf
    protected inline fun <reified T : PsiElement> initWith(code: String): List<T> {
        return PsiTreeUtil.findChildrenOfAnyType(
            parseCode(code), false, T::class.java
        ).toList()
    }

    // Asserts
    protected fun assertIsNameIdentifierALeaf(element: PsiElement?) {
        assertInstanceOf(element?.toLeaf(), LeafPsiElement::class.java)
    }

    protected fun assertQualifiedNameEquals(element: OCamlNameIdentifierOwner?, name: String) {
        assertEquals(OCAML_FILE_QUALIFIED_NAME_DOT + name, element?.qualifiedName)
    }

    // Utils
    protected fun hasError(file: PsiFile): Boolean {
        var hasErrors = false
        file.accept(object : PsiElementVisitor() {
            override fun visitElement(element: PsiElement) {
                if (element is PsiErrorElement) {
                    hasErrors = true
                    return
                }
                element.acceptChildren(this)
            }
        })
        return hasErrors
    }
}

abstract class OCamlParsingTestCase : OCamlBaseParsingTestCase("ml", OCamlParserDefinition())
abstract class OCamlInterfaceParsingTestCase : OCamlBaseParsingTestCase("mli", OCamlInterfaceParserDefinition())