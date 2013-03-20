package net.ontrack.extension.api.property;

import net.sf.jstring.Strings;

import java.util.Locale;

public abstract class AbstractLinkPropertyExtensionDescriptor extends AbstractPropertyExtensionDescriptor {

    private final String nameKey;
    private final String iconName;

    protected AbstractLinkPropertyExtensionDescriptor(String nameKey, String iconName) {
        this.nameKey = nameKey;
        this.iconName = iconName;
    }

    /**
     * Returns the key for the name.
     */
    @Override
    public String getDisplayNameKey() {
        return nameKey;
    }

    @Override
    public String toHTML(Strings strings, Locale locale, String value) {
        return String.format("<span title=\"%3$s\"><img src=\"extension/%2$s\" /> <a href=\"%1$s\">%1$s</a></span>",
                value,
                iconName,
                strings.get(locale, nameKey));
    }
}
