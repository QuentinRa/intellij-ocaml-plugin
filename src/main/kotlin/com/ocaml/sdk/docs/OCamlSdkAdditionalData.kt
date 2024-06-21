package com.ocaml.sdk.docs

import com.intellij.openapi.projectRoots.SdkAdditionalData

class OCamlSdkAdditionalData : SdkAdditionalData {
    /**
     * URL to the manual of the current SDK version of OCaml
     */
    @JvmField
    var ocamlManualURL: String? = ""

    /**
     * URL to the API of the current SDK version of OCaml
     */
    @JvmField
    var ocamlApiURL: String? = ""

    // utils
    override fun toString(): String {
        return "OCamlSdkAdditionalData{" +
                "ocamlManualURL='" + ocamlManualURL + '\'' +
                ", ocamlApiURL='" + ocamlApiURL + '\'' +
                '}'
    }

    fun shouldFillWithDefaultValues(): Boolean {
        // if empty, yes
        return ocamlApiURL == null || ocamlManualURL == null ||
                ocamlManualURL!!.isBlank() || ocamlApiURL!!.isBlank()
    }
}
