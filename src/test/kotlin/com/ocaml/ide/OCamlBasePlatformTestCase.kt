package com.ocaml.ide

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.DebugUtil
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.ocaml.ide.colors.OCamlColor
import com.ocaml.language.base.OCamlFileBase
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

// Source: https://github.com/giraud/reasonml-idea-plugin/blob/master/src/test/java/com/reason/ide/ORBasePlatformTestCase.java
// Revision: 00cc6a98d1086923a7dcd2436bdb3d61db0753ae
@RunWith(JUnit4::class)
abstract class OCamlBasePlatformTestCase : BasePlatformTestCase() {
    protected companion object {
        const val FILE_NAME = "dummy"
        const val OCAML_FILE_QUALIFIED_NAME = "Dummy"
        const val OCAML_FILE_QUALIFIED_NAME_DOT = "Dummy."
    }

    protected fun configureCode(fileName: String, code: String): OCamlFileBase {
        val file: PsiFile = myFixture.configureByText(fileName, code)
//        println("» " + fileName + " " + this.javaClass)
//        println(DebugUtil.psiToString(file, false, true))
        return file as OCamlFileBase
    }

    protected inline fun <reified T : PsiElement> configureCodeAsList(fileName: String, code: String): List<T> {
        return PsiTreeUtil.findChildrenOfType(
            configureCode(fileName, code),
            T::class.java
        ).toList()
    }

    protected inline fun <reified T : PsiElement> configureOCaml(code: String): List<T> =
        configureCodeAsList<T>("$FILE_NAME.ml", code)
    protected inline fun <reified T : PsiElement> configureOCamlInterface(code: String): List<T> =
        configureCodeAsList<T>("$FILE_NAME.mli", code)

    protected fun configureHighlight(fileName: String, code: String, color: OCamlColor, ignoreExtra: Boolean = false) {
        myFixture.configureByText(fileName, code.replace(
            "<info>",
            "<info descr=\"null\" textAttributesKey=\"${color.textAttributesKey.externalName}\">"
        ))
        myFixture.checkHighlighting(false, true, false, ignoreExtra)
    }
}