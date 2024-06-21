package com.ocaml.sdk.providers.cygwin;

import com.intellij.openapi.options.ConfigurationException;
import com.ocaml.sdk.providers.simple.SimpleSdkData;
import com.ocaml.utils.files.OCamlFileUtils;
import org.junit.Test;

import java.util.ArrayList;

public class CygwinCreateSimpleSdkTest extends CygwinBaseTest {

    public CygwinCreateSimpleSdkTest(String folderName) {
        super(folderName);
    }

    private void assertCreate(int i) {
        if (folders.BIN_CREATE_SDK == null) return;
        ArrayList<String> homes = new ArrayList<>();
        try {
            try {
                for (; i > 0 ; i--) {
                    SimpleSdkData simpleSdkData = new SimpleSdkData(
                            folders.BIN_CREATE_SDK.toplevel,
                            folders.BIN_CREATE_SDK.comp,
                            folders.BIN_CREATE_SDK.version,
                            folders.BIN_CREATE_SDK.sources
                    );
                    homes.add(simpleSdkData.homePath);
                    assertCygwinHomeValid(simpleSdkData.homePath);
                }
            } catch (ConfigurationException e) {
                fail(e.getMessage());
            }
        } finally {
            homes.forEach(OCamlFileUtils::deleteDirectory);
        }
    }

    @Test
    public void testCreate1Sdk() {
        assertCreate(1);
    }

    @Test
    public void testCreate2Sdks() {
        assertCreate(2);
    }

    @Test
    public void testCreate3Sdks() {
        assertCreate(3);
    }

}
