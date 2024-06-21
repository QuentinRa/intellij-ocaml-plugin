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

    fun createUIComponents(sdk: Sdk) {
        myDocumentationURL!!.text = getManualURL(sdk.versionString!!)
        myApiURL!!.text = getApiURL(sdk.versionString!!)

        myDocumentationURL!!.isEnabled = false
        myApiURL!!.isEnabled = false
    }

    val component: JComponent?
        get() = myMainPanel

    val isModified: Boolean
        get() {
            return false
        }

    @Throws(ConfigurationException::class)
    fun apply() {
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
