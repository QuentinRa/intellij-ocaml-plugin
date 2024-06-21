package com.ocaml.sdk.providers;

import com.intellij.openapi.diagnostic.Logger;

public interface BaseFolderProvider {

    Logger LOG = Logger.getInstance("ocaml.tests");

    String getName();
    boolean isOpamAvailable();
    boolean isBinAvailable();

}
