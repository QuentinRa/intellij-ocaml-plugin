package com.ocaml.sdk.providers.windows;

import com.ocaml.sdk.providers.OCamlSdkProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class WindowProviderUtil {

    @Contract(" -> new")
    public static @NotNull OCamlSdkProvider @NotNull [] createWindowsProviders() {
        return new OCamlSdkProvider[]{
                new CygwinSdkProvider(),
                new OCaml64SdkProvider()
        };

    }
}
