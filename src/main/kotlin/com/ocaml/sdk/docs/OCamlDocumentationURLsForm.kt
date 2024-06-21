package com.ocaml.sdk.docs

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.ui.components.ActionLink
import com.ocaml.sdk.utils.OCamlSdkWebsiteUtils.getApiURL
import com.ocaml.sdk.utils.OCamlSdkWebsiteUtils.getManualURL
import java.awt.event.ActionListener
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

/**
 * Show the form to change the manual/API URL
 * (a.k.a. documentation URLs).
 */
class OCamlDocumentationURLsForm {
    private var myMainPanel: JPanel? = null
    private var myDocumentationURL: JTextField? = null
    private var myApiURL: JTextField? = null
    private var myDocBefore: ActionLink? = null
    private var myDocAfter: ActionLink? = null
    private var myApiBefore: ActionLink? = null
    private var myApiAfter: ActionLink? = null

    private var myData: OCamlSdkAdditionalData? = null

    fun createUIComponents(mySdk: Sdk) {
        myData = mySdk.sdkAdditionalData as OCamlSdkAdditionalData?
        if (myData == null) return  // should never happen

        myDocumentationURL!!.text = myData!!.ocamlManualURL
        myApiURL!!.text = myData!!.ocamlApiURL
    }

    val component: JComponent?
        get() = myMainPanel

    val isModified: Boolean
        get() {
            if (myData != null) {
                return (myDocumentationURL!!.text.trim { it <= ' ' } != myData!!.ocamlManualURL
                        || myApiURL!!.text.trim { it <= ' ' } != myData!!.ocamlApiURL)
            }
            return false
        }

    @Throws(ConfigurationException::class)
    fun apply() {
        if (myData != null) {
            myData!!.ocamlManualURL = myDocumentationURL!!.text.trim { it <= ' ' }
            myData!!.ocamlApiURL = myApiURL!!.text.trim { it <= ' ' }
        }
    }

    private fun createUIComponents() {
        val docBefore = getManualURL("4.10")
        val docAfter: String = getManualURL("4.12")
        val apiBefore: String = getApiURL("4.10")
        val apiAfter: String = getApiURL("4.12")

        myDocBefore = ActionLink(docBefore, ActionListener { BrowserUtil.browse(docBefore) })
        myDocBefore!!.setExternalLinkIcon()

        myDocAfter = ActionLink(docAfter, ActionListener { BrowserUtil.browse(docAfter) })
        myDocAfter!!.setExternalLinkIcon()

        myApiBefore = ActionLink(apiBefore, ActionListener { BrowserUtil.browse(apiBefore) })
        myApiBefore!!.setExternalLinkIcon()

        myApiAfter = ActionLink(apiAfter, ActionListener { BrowserUtil.browse(apiAfter) })
        myApiAfter!!.setExternalLinkIcon()
    }
}
