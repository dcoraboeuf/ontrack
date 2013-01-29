package net.ontrack.web.gui;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ontrack.core.model.Entity;
import net.ontrack.core.model.EntityStub;
import net.ontrack.core.model.ExpandedEvent;
import net.ontrack.core.ui.EventUI;
import net.ontrack.web.gui.model.GUIEvent;
import net.sf.jstring.Strings;

import org.apache.commons.lang3.StringUtils;
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
public class EventController {
	
	private final Pattern pattern = Pattern.compile("($[^$.]+$)");

	private final EventUI eventUI;
	private final Strings strings;

	@Autowired
	public EventController(EventUI eventUI, Strings strings) {
		this.eventUI = eventUI;
		this.strings = strings;
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public @ResponseBody
	List<GUIEvent> all(final Locale locale, @RequestParam(required = false, defaultValue = "0") int offset, @RequestParam(required = false, defaultValue = "20") int count) {
		// Gets the raw events
		List<ExpandedEvent> events = eventUI.all(offset, count);
		// Localizes them
		List<GUIEvent> guiEvents = Lists.transform(events, new Function<ExpandedEvent, GUIEvent>() {
			@Override
			public GUIEvent apply (ExpandedEvent event) {
				return toGUIEvent (event, locale);
			}
		});
		// OK
		return guiEvents;
	}

	protected GUIEvent toGUIEvent(ExpandedEvent event, Locale locale) {
		// Generating the HTML
		// Getting the general pattern from the localization strings
		String canvas = strings.get(locale, "event." + event.getEventType().name());
		// Replacing the $...$ tokens
		Matcher m = pattern.matcher(canvas);
		StringBuffer html = new StringBuffer();
		while (m.find()) {
			String value = expandToken(m.group(), event);
			m.appendReplacement(html, value);
		}
		m.appendTail(html); 
		
		// OK
		return new GUIEvent (event.getId(), event.getEventType(), event.getTimestamp(), html.toString());
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
		if (StringUtils.isAllUpperCase(key)) {
			// Gets the entity
			Entity entity = Entity.valueOf(key);
			EntityStub entityStub = event.getEntities().get(entity);
			if (entityStub == null) {
				// TODO Uses a proper exception
				throw new IllegalStateException("Could not find entity " + key + " in event " + event.getId());
			} else {
				return createLink (entity, entityStub, alternative);
			}
		}
		// FIXME Looks for a fixed value
		else {
//			String value = event.getValues().get(key);
//			if (value == null) {
				// TODO Uses a proper exception
				throw new IllegalStateException("Could not find value " + key + " in event " + event.getId());
//			} else {
//				return StringEscapeUtils.escapeHtml4(value);
//			}
		}
	}

	protected String createLink(Entity entity, EntityStub entityStub, String alternative) {
		// Text
		String text = alternative != null ? alternative : entityStub.getName();
		// Href
		String href = createLinkHref(entity, entityStub);
		// Link
		return String.format("<a href=\"{1}\">{0}</a>", text, href);
	}

	protected String createLinkHref(Entity entity, EntityStub entityStub) {
		return String.format("gui/{0}/{1}", entity.name().toLowerCase(), entityStub.getId());
	}

}
