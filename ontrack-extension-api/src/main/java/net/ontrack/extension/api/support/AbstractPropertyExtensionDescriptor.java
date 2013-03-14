package net.ontrack.extension.api.support;

import net.ontrack.core.support.InputException;
import net.ontrack.extension.api.PropertyExtensionDescriptor;
import org.apache.commons.lang3.StringEscapeUtils;

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
     * @param value Value to render
     * @return Escaped HTML value
     */
    @Override
    public String toHTML(String value) {
        return StringEscapeUtils.escapeHtml4(value);
    }
}
