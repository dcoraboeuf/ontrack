package net.ontrack.web.ui;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ontrack.core.model.*;
import net.ontrack.core.ui.EventUI;
import net.ontrack.web.gui.model.GUIEvent;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/gui/event")
public class EventController extends AbstractUIController {

	private final Pattern replacementPattern = Pattern.compile("(\\$[^$.]+\\$)");
	private final Pattern entityPattern = Pattern.compile("[A-Z_]+");

	private final EventUI eventUI;
	
	@Autowired
	public EventController(ErrorHandler errorHandler, Strings strings, EventUI eventUI) {
		super(errorHandler, strings);
		this.eventUI = eventUI;
	}

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	List<GUIEvent> all(
			final Locale locale,
			@RequestParam(required = false, defaultValue = "0") int project,
			@RequestParam(required = false, defaultValue = "0") int branch,
			@RequestParam(required = false, defaultValue = "0") int validationStamp,
            @RequestParam(required = false, defaultValue = "0") int build,
            @RequestParam(required = false, defaultValue = "0") int validationRun,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "20") int count) {
		// Reference time
		final DateTime now = new DateTime();
		// Filter
		EventFilter filter = new EventFilter(offset, count);
		filter.withEntity(Entity.PROJECT, project);
		filter.withEntity(Entity.BRANCH, branch);
		filter.withEntity(Entity.VALIDATION_STAMP, validationStamp);
        filter.withEntity(Entity.BUILD, build);
        filter.withEntity(Entity.VALIDATION_RUN, validationRun);
		// Gets the raw events
		List<ExpandedEvent> events = eventUI.list(filter);
		// Localizes them
		List<GUIEvent> guiEvents = Lists.transform(events, new Function<ExpandedEvent, GUIEvent>() {
			@Override
			public GUIEvent apply (ExpandedEvent event) {
				return toGUIEvent (event, locale, now);
			}
		});
		// OK
		return guiEvents;
	}

	protected GUIEvent toGUIEvent(ExpandedEvent event, Locale locale, DateTime now) {
		// Formatted timestamp
		String timestamp = DateTimeFormat.mediumDateTime().withLocale(locale).print(event.getTimestamp());
		// Formatted elapsed time
		Period period = new Period(event.getTimestamp(), now);
		period = compress(period);
		String elapsed = PeriodFormat.wordBased(locale).print(period);
		elapsed = strings.get(locale, "event.ago", elapsed);
		
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
		EntityStub stamp = entities.get(Entity.VALIDATION_STAMP);
		if (stamp != null) {
			icon = String.format("gui/validation_stamp/%s/%s/%s/image",
					entities.get(Entity.PROJECT).getName(),
					entities.get(Entity.BRANCH).getName(),
					stamp.getName());
		}

        // Status --> status class
        String statusValue = event.getValues().get("status");
        if (StringUtils.isNotBlank(statusValue)) {
            status = statusValue;
        }
		
		// OK
		return new GUIEvent (event.getId(), event.getAuthor(), event.getEventType(), timestamp, elapsed, html.toString(), icon, status);
	}
	
	protected Period compress(Period period) {
		Period p;
		if (period.getYears() > 0) {
			p = period.withMonths(0).withWeeks(0).withDays(0).withHours(0).withMinutes(0).withSeconds(0).withMillis(0);
		} else if (period.getMonths() > 0) {
			p = period.withWeeks(0).withDays(0).withHours(0).withMinutes(0).withSeconds(0).withMillis(0);			
		} else if (period.getWeeks() > 0) {
			p = period.withDays(0).withHours(0).withMinutes(0).withSeconds(0).withMillis(0);
		} else if (period.getDays() > 0) {
			p = period.withHours(0).withMinutes(0).withSeconds(0).withMillis(0);
		} else if (period.getHours() > 0) {
			p = period.withMinutes(0).withSeconds(0).withMillis(0);
		} else if (period.getMinutes() > 0) {
			p = period.withSeconds(0).withMillis(0);
		} else {
			p = period.withMillis(0);
		}
		return p;
	}

	protected String expandToken (String rawToken, ExpandedEvent event) {
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
			// Gets the entity
			Entity entity = Entity.valueOf(key);
			EntityStub entityStub = event.getEntities().get(entity);
			if (entityStub == null) {
				// TODO Uses a proper exception
				throw new IllegalStateException("Could not find entity " + key + " in event " + event.getId());
			} else {
				return createLink (entity, entityStub, alternative, event.getId(), event.getEntities());
			}
		}
		// Looks for a fixed value
		else {
			String value = event.getValues().get(key);
			if (value == null) {
				// TODO Uses a proper exception
				throw new IllegalStateException("Could not find value " + key + " in event " + event.getId());
			} else {
				return format("<span class=\"event-value\">%s</span>", escapeHtml4(value));
			}
		}
	}

	protected String createLink(Entity entity, EntityStub entityStub, String alternative, int eventId, Map<Entity,EntityStub> contextEntities) {
		// Text
		String text = alternative != null ? alternative : entityStub.getName();
		text = StringEscapeUtils.escapeHtml4(text);
		// Href
		String href = createLinkHref(entity, entityStub, eventId, contextEntities);
		// Link
		return format("<a class=\"event-entity\" href=\"%s\">%s</a>", href, text);
	}

	protected String createLinkHref(Entity entity, EntityStub entityStub, int eventId, Map<Entity,EntityStub> contextEntities) {
		// Start of the URI
		StringBuilder uri = new StringBuilder("gui/").append(entity.name().toLowerCase());
		// For each context entity
		for (Entity contextEntity : entity.getContext()) {
			// Gets the context stub
			EntityStub contextStub = contextEntities.get(contextEntity);
			if (contextStub == null) {
				// TODO Uses a proper exception
				throw new IllegalStateException("Could not find entity " + contextEntity + " in event " + eventId);
			}
			// Adds it to the URI
			uri.append("/").append(contextStub.getName());
		}
		// Appends this entity to the URI
		uri.append("/").append(entityStub.getName());
		// OK
		return uri.toString();
	}

}
