package com.ocaml.sdk.docs

import com.intellij.openapi.projectRoots.SdkAdditionalData

class OCamlSdkAdditionalData : SdkAdditionalData {
    private var markedAsCommited = false

    /**
     * URL to the manual of the current SDK version of OCaml
     */
    var ocamlManualURL: String? = ""
        set(value) {
            if (!markedAsCommited)
                field = value
            else error("OCaml SDK Additional data was already committed")
        }

    /**
     * URL to the API of the current SDK version of OCaml
     */
    var ocamlApiURL: String? = ""
        set(value) {
            if (!markedAsCommited)
                field = value
            else error("OCaml SDK Additional data was already committed")
        }

    override fun markAsCommited() {
        super.markAsCommited()
        markedAsCommited = true
    }

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
