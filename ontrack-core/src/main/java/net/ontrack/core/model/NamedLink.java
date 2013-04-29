package net.ontrack.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NamedLink {

    private final String url;
    private final String title;
    private final String icon;
    private final String css;

    public NamedLink(String url, String title) {
        this(url, title, null, null);
    }

    public NamedLink withIcon(String icon) {
        return new NamedLink(url, title, icon, css);
    }

    public NamedLink withCss(String css) {
        return new NamedLink(url, title, icon, css);
    }
}
