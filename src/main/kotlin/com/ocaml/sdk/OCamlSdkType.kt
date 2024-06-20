package com.ocaml.sdk

import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.ui.configuration.projectRoot.SdkDownload
import com.intellij.openapi.roots.ui.configuration.projectRoot.SdkDownloadTask
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.ocaml.icons.OCamlIcons
import com.ocaml.sdk.utils.OCamlSdkHomeUtils
import com.ocaml.sdk.utils.OCamlSdkRootsUtils
import com.ocaml.sdk.utils.OCamlSdkVersionUtils
import org.jdom.Element
import java.io.File
import java.util.function.Consumer
import javax.swing.Icon
import javax.swing.JComponent

/**
 * OCaml SDK
 *
 *  * .opam-switch/sources/ *
 *  * bin/ocaml (.exe allowed)
 *  * bin/ocamlc (.exe allowed)
 *  * lib/ *
 *
 */
class OCamlSdkType : SdkType(OCAML_SDK), SdkDownload {
    //
    // Name + Icon
    //
    override fun getPresentableName(): String {
        return "OCaml"
    }

    override fun getIcon(): Icon {
        return OCamlIcons.Nodes.OCAML_SDK
    }

    //
    // Home path
    //
    override fun suggestHomePaths(): Collection<String> {
        return OCamlSdkHomeUtils.suggestHomePaths()
    }

    override fun suggestHomePath(): String? {
        return OCamlSdkHomeUtils.defaultOCamlLocation()
    }

    //
    // suggestSdkName, getVersionString
    //
    override fun suggestSdkName(currentSdkName: String?, sdkHome: String): String {
        return "OCaml-" + getVersionString(sdkHome)
    }

    override fun getVersionString(sdkHome: String): String {
        return OCamlSdkVersionUtils.parse(sdkHome)
    }

    //
    // Valid
    //
    override fun isValidSdkHome(sdkHome: String): Boolean {
        return OCamlSdkHomeUtils.isValid(sdkHome)
    }

//    override fun getInvalidHomeMessage(path: String): String {
//        val kind: InvalidHomeError = OCamlSdkHomeUtils.invalidHomeErrorMessage(Path.of(path))
//            ?: return message("sdk.home.error.no.provider")
//        return when (kind) {
//            INVALID_HOME_PATH -> message("sdk.home.error.invalid")
//            NO_TOP_LEVEL -> message("sdk.home.error.no.top.level")
//            NO_COMPILER -> message("sdk.home.error.no.compiler")
//            NO_SOURCES -> message("sdk.home.error.no.sources")
//            NONE, GENERIC -> super.getInvalidHomeMessage(path)
//            else -> super.getInvalidHomeMessage(path)
//        }
//    }

    //
    // Data
    //
    override fun isRootTypeApplicable(type: OrderRootType): Boolean {
        return type === OrderRootType.CLASSES
    }

    override fun createAdditionalDataConfigurable(
        sdkModel: SdkModel,
        sdkModificator: SdkModificator
    ): AdditionalDataConfigurable? {
//        return OCamlSdkAdditionalDataConfigurable()
        return null
    }

    override fun loadAdditionalData(currentSdk: Sdk, additional: Element): SdkAdditionalData? {
//        val sdkAdditionalData: OCamlSdkAdditionalData = OCamlSdkAdditionalData()
//        sdkAdditionalData.ocamlManualURL = additional.getAttributeValue("ocamlManualURL")
//        sdkAdditionalData.ocamlApiURL = additional.getAttributeValue("ocamlApiURL")
//        if (sdkAdditionalData.shouldFillWithDefaultValues()) {
//            sdkAdditionalData.ocamlApiURL = getDefaultAPIUrl(currentSdk)
//            sdkAdditionalData.ocamlManualURL = getDefaultDocumentationUrl(currentSdk)
//        }
//        return sdkAdditionalData
        return null
    }

    override fun saveAdditionalData(
        additionalData: SdkAdditionalData,
        additional: Element
    ) {
//        val sdkAdditionalData: OCamlSdkAdditionalData = additionalData as OCamlSdkAdditionalData
//        additional.setAttribute("ocamlManualURL", sdkAdditionalData.ocamlManualURL)
//        additional.setAttribute("ocamlApiURL", sdkAdditionalData.ocamlApiURL)
    }

    //
    // Setup
    //
    override fun setupSdkPaths(sdk: Sdk) {
        val homePath = checkNotNull(sdk.homePath) { sdk }
        val sdkModificator = sdk.sdkModificator
        sdkModificator.removeRoots(OrderRootType.CLASSES)
        addSources(File(homePath), sdkModificator)
        // 0.0.6 - added by default
        sdkModificator.addRoot(getDefaultDocumentationUrl(sdk), OrderRootType.DOCUMENTATION)
        sdkModificator.addRoot(getDefaultAPIUrl(sdk), OrderRootType.DOCUMENTATION)
        sdkModificator.commitChanges()
    }

    //
    // Documentation
    //
    override fun getDefaultDocumentationUrl(sdk: Sdk): String {
        return getManualURL(sdk.versionString)
    }

    fun getDefaultAPIUrl(sdk: Sdk): String {
        return getApiURL(sdk.versionString)
    }

    //
    // Download
    //
    override fun supportsDownload(sdkTypeId: SdkTypeId): Boolean {
        return false
    }

    override fun showDownloadUI(
        sdkTypeId: SdkTypeId,
        sdkModel: SdkModel,
        parentComponent: JComponent,
        selectedSdk: Sdk?,
        sdkCreatedCallback: Consumer<in SdkDownloadTask>
    ) {
    }

    //
    // WSL
    //
    @Suppress("unused")
    override fun allowWslSdkForLocalProject(): Boolean {
        return true
    }

    companion object {
        private const val OCAML_SDK = "OCaml SDK"

        val instance: OCamlSdkType?
            get() = EP_NAME.findExtension(OCamlSdkType::class.java)

        fun getApiURL(version: String?): String {
            var version = version
            version = getMajorAndMinorVersion(version!!)
            if (version == null) version = "4.12"
            if (OCamlSdkVersionUtils.isNewerThan(
                    "4.12",
                    version
                )
            ) return "https://ocaml.org/releases/$version/api/index.html"
            return "https://ocaml.org/releases/$version/htmlman/libref/index.html"
        }

        fun getManualURL(version: String?): String {
            var version = version
            version = getMajorAndMinorVersion(version!!)
            if (version == null) version = "4.12"
            if (OCamlSdkVersionUtils.isNewerThan(
                    "4.12",
                    version
                )
            ) return "https://ocaml.org/releases/$version/manual/index.html"
            return "https://ocaml.org/releases/$version/htmlman/index.html"
        }

        //
        // Sources
        //
        fun addSources(sdkHomeFile: File, sdkModificator: SdkModificator) {
            val sources: List<String> = OCamlSdkRootsUtils.getSourcesFolders(sdkHomeFile.path)
            for (source in sources) {
                addSources(source, sdkHomeFile, sdkModificator)
            }
        }

        private fun addSources(sourceName: String, sdkHomeFile: File, sdkModificator: SdkModificator) {
            val rootFolder = File(sdkHomeFile, sourceName)
            if (!rootFolder.exists()) return

            val files = rootFolder.listFiles() ?: return
            for (file in files) {
                val rootCandidate = LocalFileSystem.getInstance()
                    .findFileByPath(FileUtil.toSystemIndependentName(file.absolutePath))
                if (rootCandidate == null) continue
                sdkModificator.addRoot(rootCandidate, OrderRootType.CLASSES)
            }
        }

        fun getMajorAndMinorVersion(version: String): String? {
            var version = version
            if (!OCamlSdkVersionUtils.isValid(version)) return null
            // if we got two ".", then we trunc the patch number
            val last = version.lastIndexOf('.')
            if (last != version.indexOf('.')) version = version.substring(0, last)
            return version
        }
    }
}