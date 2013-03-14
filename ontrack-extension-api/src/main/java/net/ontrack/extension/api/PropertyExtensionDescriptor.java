package net.ontrack.extension.api;

import net.ontrack.core.model.Entity;
import net.ontrack.core.support.InputException;
import net.sf.jstring.Strings;

import java.util.EnumSet;
import java.util.Locale;

public interface PropertyExtensionDescriptor {

    /**
     * Scope of the property
     *
     * @return List of {@link Entity} this property can apply to
     */
    EnumSet<Entity> getScope();

    /**
     * Validates a value
     *
     * @param value Value to validate
     * @throws InputException If not valid
     */
    void validate(String value) throws InputException;

    /**
     * Name of the associated extension
     *
     * @return Extension ID
     */
    String getExtension();

    /**
     * Name of the property
     *
     * @return Property name
     */
    String getName();

    /**
     * Given a value, renders it as HTML.
     *
     * @param strings Localization
     * @param locale Locale to render into
     * @param value Value to render
     * @return HTML to display
     */
    String toHTML(Strings strings, Locale locale, String value);
}
