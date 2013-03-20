package net.ontrack.extension.api.property;

import net.ontrack.core.model.Entity;
import net.ontrack.core.support.InputException;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Locale;

import static java.lang.String.format;

public abstract class AbstractPropertyExtensionDescriptor implements PropertyExtensionDescriptor {

    /**
     * Does not validate any thing by default.
     */
    @Override
    public void validate(String value) throws InputException {
    }

    /**
     * Returns an escaped value
     */
    @Override
    public String toHTML(Strings strings, Locale locale, String value) {
        return StringEscapeUtils.escapeHtml4(value);
    }

    /**
     * Just a text field
     */
    @Override
    public String editHTML(Strings strings, Locale locale, String value) {
        return format(
                "<input id=\"extension-%1$s-%2$s\" name=\"extension-%1$s-%2$s\" type=\"text\" maxlength=\"200\" class=\"input-xxlarge\" value=\"%3$s\" />",
                getExtension(), // 1
                getName(), // 2
                value != null ? StringEscapeUtils.escapeHtml4(value) : "" // 3
        );
    }

    /**
     * Not editable by default; returns <code>null</code>.
     */
    @Override
    public String getRoleForEdition(Entity entity) {
        return null;
    }
}
