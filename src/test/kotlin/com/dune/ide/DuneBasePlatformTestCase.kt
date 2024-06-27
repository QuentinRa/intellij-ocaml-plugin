package com.dune.ide

import com.dune.ide.colors.DuneColor
import com.dune.language.psi.files.DuneFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
abstract class DuneBasePlatformTestCase : BasePlatformTestCase() {

    protected fun configureCode(fileName: String, code: String): DuneFile {
        val file: PsiFile = myFixture.configureByText(fileName, code)
//        println("» " + fileName + " " + this.javaClass)
//        println(DebugUtil.psiToString(file, false, true))
        return file as DuneFile
    }

    protected inline fun <reified T : PsiElement> configureCodeAsList(fileName: String, code: String): List<T> {
        return PsiTreeUtil.findChildrenOfType(
            configureCode(fileName, code),
            T::class.java
        ).toList()
    }

    protected inline fun <reified T : PsiElement> configureDune(code: String): List<T> =
        configureCodeAsList<T>("dune", code)

    protected inline fun <reified T : PsiElement> configureDuneProject(code: String): List<T> =
        configureCodeAsList<T>("dune-project", code)

    protected fun configureHighlight(code: String, color: DuneColor, ignoreExtra: Boolean = false) {
        myFixture.configureByText("dune", code.replace(
            "<info>",
            "<info descr=\"null\" textAttributesKey=\"${color.textAttributesKey.externalName}\">"
        ))
        myFixture.checkHighlighting(false, true, false, ignoreExtra)
    }
}