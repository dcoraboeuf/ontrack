package net.ontrack.extension.api.support;

import net.ontrack.core.support.InputException;
import net.ontrack.extension.api.PropertyExtensionDescriptor;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Locale;

public abstract class AbstractPropertyExtensionDescriptor implements PropertyExtensionDescriptor {

    /**
     * Does not validate any thing by default.
     */
    @Override
    public void validate(String value) throws InputException {
    }

    /**
     * Returns an escaped value
     *
     * @param strings Localization
     * @param locale Locale to render into
     * @param value  Value to render
     * @return Escaped HTML value
     */
    @Override
    public String toHTML(Strings strings, Locale locale, String value) {
        return StringEscapeUtils.escapeHtml4(value);
    }
}
