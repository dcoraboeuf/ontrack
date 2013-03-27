package net.ontrack.backend;

import net.ontrack.core.model.Entity;
import net.ontrack.core.model.EntityStub;
import net.ontrack.core.model.ExpandedEvent;
import net.ontrack.core.model.GUIEvent;
import net.ontrack.core.support.TimeUtils;
import net.ontrack.service.GUIEventService;
import net.ontrack.service.GUIService;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

@Service
public class DefaultGUIEventService implements GUIEventService {

    private final Pattern replacementPattern = Pattern.compile("(\\$[^$.]+\\$)");
    private final Pattern entityUriPattern = Pattern.compile("\\{([a-z_]+)\\}");
    private final Pattern entityPattern = Pattern.compile("[A-Z_]+");

    private final GUIService guiService;
    private final Strings strings;

    @Autowired
    public DefaultGUIEventService(GUIService guiService, Strings strings) {
        this.guiService = guiService;
        this.strings = strings;
    }

    @Override
    public GUIEvent toGUIEvent(ExpandedEvent event, Locale locale, DateTime now) {
        // Formatted timestamp
        String timestamp = TimeUtils.format(locale, event.getTimestamp());
        // Formatted elapsed time
        String elapsed = TimeUtils.elapsed(strings, locale, event.getTimestamp(), now, event.getAuthor());

        // Generating the HTML
        // Getting the general pattern from the localization strings
        String canvas = strings.get(locale, "event." + event.getEventType().name());
        // Replacing the $...$ tokens
        Matcher m = replacementPattern.matcher(canvas);
        StringBuffer html = new StringBuffer();
        while (m.find()) {
            String value = expandToken(m.group(), event);
            m.appendReplacement(html, value);
        }
        m.appendTail(html);

        // Icon & status
        String icon = "";
        String status = "";

        // Stamp --> icon
        Map<Entity, EntityStub> entities = event.getEntities();
        EntityStub entity = entities.get(Entity.VALIDATION_STAMP);
        if (entity != null) {
            icon = String.format("gui/project/%s/branch/%s/validation_stamp/%s/image",
                    entities.get(Entity.PROJECT).getName(),
                    entities.get(Entity.BRANCH).getName(),
                    entity.getName());
        } else {
            // Promotion level --> icon
            entity = entities.get(Entity.PROMOTION_LEVEL);
            if (entity != null) {
                icon = String.format("gui/project/%s/branch/%s/promotion_level/%s/image",
                        entities.get(Entity.PROJECT).getName(),
                        entities.get(Entity.BRANCH).getName(),
                        entity.getName());
            }
        }

        // Status --> status class
        String statusValue = event.getValues().get("status");
        if (StringUtils.isNotBlank(statusValue)) {
            status = statusValue;
        }

        // OK
        return new GUIEvent(event.getId(), event.getAuthor(), event.getEventType(), timestamp, elapsed, html.toString(), icon, status);
    }

    protected String expandToken(String rawToken, ExpandedEvent event) {
        // Gets rid of the $...$
        String token = StringUtils.substring(rawToken, 1, -1);
        // Searches for alternate display
        String key = token;
        String alternative = null;
        int pipe = token.indexOf('|');
        if (pipe > 0) {
            key = token.substring(0, pipe);
            alternative = token.substring(pipe + 1);
        }
        // Looks for an entity stub
        if (entityPattern.matcher(key).matches()) {
            return expandEntityToken(event, key, alternative);

        }
        // Looks for a fixed value
        else {
            return expandValueToken(event, key);
        }
    }

    private String expandValueToken(ExpandedEvent event, String key) {
        // Special values
        if ("author".equals(key)) {
            return format("<span class=\"event-author\">%s</span>", escapeHtml4(event.getAuthor()));
        } else {
            String value = event.getValues().get(key);
            if (value == null) {
                // TODO Uses a proper exception
                throw new IllegalStateException("Could not find value " + key + " in event " + event.getId());
            } else {
                return format("<span class=\"event-value\">%s</span>", escapeHtml4(value));
            }
        }
    }

    private String expandEntityToken(ExpandedEvent event, String key, String alternative) {
        // Gets the entity
        Entity entity = Entity.valueOf(key);
        EntityStub entityStub = event.getEntities().get(entity);
        if (entityStub == null) {
            // TODO Uses a proper exception
            throw new IllegalStateException("Could not find entity " + key + " in event " + event.getId());
        } else {
            return createLink(entity, entityStub, alternative, event.getEntities());
        }
    }

    protected String createLink(Entity entity, EntityStub entityStub, String alternative, Map<Entity, EntityStub> contextEntities) {
        // Text
        String text = alternative != null ? alternative : entityStub.getName();
        text = StringEscapeUtils.escapeHtml4(text);
        // Href
        String href = createLinkHref(entity, entityStub, contextEntities);
        // Link
        return format("<a class=\"event-entity\" href=\"%s\">%s</a>", href, text);
    }

    protected String createLinkHref(Entity entity, EntityStub entityStub, Map<Entity, EntityStub> contextEntities) {
        // Gets the URI pattern of the entity
        String uriPattern = entity.getUriPattern();
        // Looks for replacements
        Matcher m = entityUriPattern.matcher(uriPattern);
        StringBuffer uri = new StringBuffer();
        while (m.find()) {
            String name = m.group(1);
            String value = getEntityName(entityStub, contextEntities, name);
            m.appendReplacement(uri, value);
        }
        m.appendTail(uri);

        // Start of the URL
        return guiService.toGUI(uri.toString());
    }

    private String getEntityName(EntityStub entityStub, Map<Entity, EntityStub> contextEntities, String name) {
        if ("this".equals(name)) {
            return entityStub.getName();
        } else {
            EntityStub stub = contextEntities.get(Entity.valueOf(StringUtils.upperCase(name)));
            if (stub == null) {
                // TODO Uses a proper exception
                throw new IllegalStateException("Could not find entity " + name);
            } else {
                return stub.getName();
            }
        }
    }
}
