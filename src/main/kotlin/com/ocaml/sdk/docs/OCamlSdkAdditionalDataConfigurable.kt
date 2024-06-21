package com.ocaml.sdk.docs

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable
import com.intellij.openapi.projectRoots.Sdk
import com.ocaml.sdk.OCamlSdkType
import javax.swing.JComponent

/**
 * When we are in Project Structures > SDKs, setSdk will be called for every OCaml
 * Sdk in the list. We are supposed to create a view for the additional data, for each SDK.
 */
class OCamlSdkAdditionalDataConfigurable : AdditionalDataConfigurable {
    private val myView = OCamlDocumentationURLsForm()
    private var mySdk: Sdk? = null

    override fun getTabName(): String = "Documentation"

    override fun setSdk(sdk: Sdk) {
        mySdk = sdk
    }

    override fun createComponent(): JComponent? {
        if (mySdk != null) {
            myView.createUIComponents(mySdk!!)
            return myView.component
        }
        return null
    }

    override fun isModified(): Boolean {
        return myView.isModified
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        myView.apply()
    }
}
