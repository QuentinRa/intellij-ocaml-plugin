package com.ocaml.utils.adaptor.ui;

import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
import com.ocaml.utils.adaptor.SinceIdeVersion;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class SimpleColoredComponentAdaptor {

    /**
     * Convert HTML text to colored fragments and add all this fragments to current component.
     * @param component the SimpleColoredComponent component
     * @param html html string for supported html tags see {@link HtmlToSimpleColoredComponentConverter}
     * @param baseAttributes attributes which will be base for parsed fragments
     */
    @SinceIdeVersion(release = "211")
    public static void appendHTML(@NotNull SimpleColoredComponent component,
                           @Nls String html,
                           SimpleTextAttributes baseAttributes) {
        new HtmlToSimpleColoredComponentConverter().appendHtml(component, html, baseAttributes);
    }

}
