package net.ontrack.extension.api.property;

import net.ontrack.core.model.Entity;
import net.ontrack.core.security.AuthorizationPolicy;
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
     * Key for the display name
     */
    String getDisplayNameKey();

    /**
     * Key for the display name
     */
    String getDisplayDescriptionKey();

    /**
     * Relative path to an icon
     */
    String getIconPath();

    /**
     * Given a value, renders it as HTML.
     *
     * @param strings Localization
     * @param locale  Locale to render into
     * @param value   Value to render
     * @return HTML to display
     */
    String toHTML(Strings strings, Locale locale, String value);

    /**
     * Can this property be directly edited by a used on the given
     * associated entity.
     *
     * @param entity Entity where to edit the property
     * @return Authorization policy for this entity
     */
    AuthorizationPolicy getEditingAuthorizationPolicy(Entity entity);

    /**
     * Defines the authorization policy for viewing this property.
     */
    AuthorizationPolicy getViewingAuthorizationPolicy(Entity entity);

    /**
     * Gets the HTML fragment that allows for the edition of this value.
     * The field that contains the value must have the HTML ID <code>extension-{extension}-{name}</code>
     * where {extension} and {name} identify this extension.
     *
     * @param strings Localization
     * @param locale  Locale to render into
     * @param value   Value to edit
     * @return HTML to display
     */
    String editHTML(Strings strings, Locale locale, String value);
}
