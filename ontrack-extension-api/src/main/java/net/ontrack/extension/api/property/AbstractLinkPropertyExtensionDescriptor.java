package net.ontrack.extension.api.property;

import net.ontrack.core.support.InputException;
import net.ontrack.core.model.Entity;
import net.sf.jstring.Strings;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

public abstract class AbstractLinkPropertyExtensionDescriptor extends AbstractPropertyExtensionDescriptor {

    private final String nameKey;
    private final String iconName;

    protected AbstractLinkPropertyExtensionDescriptor(String nameKey, String iconName) {
        this.nameKey = nameKey;
        this.iconName = iconName;
    }

    /**
     * Only valid URLs are accepted
     */
    @Override
    public void validate(String value) throws InputException {
        try {
            // URI validation
            new URI(value);
            // URL validation
            new URL(value);
        } catch (MalformedURLException | URISyntaxException e) {
            throw new InvalidURLException(value);
        }
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
