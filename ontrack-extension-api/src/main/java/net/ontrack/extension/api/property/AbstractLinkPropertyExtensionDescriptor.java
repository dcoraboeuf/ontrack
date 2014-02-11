package net.ontrack.extension.api.property;

import net.ontrack.core.model.Entity;
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
    public String getIconPath() {
        return "extension/" + iconName;
    }

    @Override
    public String toHTML(Strings strings, Locale locale, Entity entity, int entityId, String value) {
        return String.format(
                "<a href=\"%1$s\">%1$s</a>",
                value);
    }
}
