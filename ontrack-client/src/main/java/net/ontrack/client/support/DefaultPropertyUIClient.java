package net.ontrack.client.support;

import net.ontrack.client.PropertyUIClient;
import net.ontrack.core.model.*;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static java.lang.String.format;

public class DefaultPropertyUIClient extends AbstractClient implements PropertyUIClient {

    public DefaultPropertyUIClient(String url) {
        super(url);
    }

    @Override
    public Ack saveProperty(Entity entity, int entityId, String extension, String name, PropertyForm form) {
        return post(
                Locale.getDefault(),
                format(
                        "/ui/property/%s/%d/edit/%s/%s",
                        entity.name(),
                        entityId,
                        extension,
                        name
                ),
                Ack.class,
                form);
    }

    @Override
    public List<DisplayablePropertyValue> getProperties(Locale locale, Entity entity, int entityId) {
        return list(
                locale,
                format(
                        "/ui/property/%s/%d",
                        entity.name(),
                        entityId
                ),
                DisplayablePropertyValue.class);
    }

    @Override
    public List<DisplayableProperty> getPropertyList(Locale locale, Entity entity) {
        return list(
                locale,
                format(
                        "/ui/property/%s",
                        entity.name()
                ),
                DisplayableProperty.class);
    }

    @Override
    public List<EditableProperty> getEditableProperties(Locale locale, Entity entity, int entityId) {
        return list(
                locale,
                format(
                        "/ui/property/%s/%d/editable",
                        entity.name(),
                        entityId
                ),
                EditableProperty.class);
    }

    @Override
    public String getPropertyValue(Entity entity, int entityId, String extension, String name) {
        return get(
                getDefaultLocale(),
                format(
                        "/ui/property/%s/%d/%s/%s",
                        entity.name(),
                        entityId,
                        extension,
                        name
                ),
                String.class
        );
    }

    @Override
    public Collection<Integer> getEntitiesForPropertyValue(Entity entity, String extension, String name, String value) {
        return list(
                getDefaultLocale(),
                format(
                        "/ui/property/search/%s/%s/%s/%s",
                        entity.name(),
                        extension,
                        name,
                        value
                ),
                Integer.class
        );
    }
}
