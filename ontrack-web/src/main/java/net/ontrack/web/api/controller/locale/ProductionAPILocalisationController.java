package net.ontrack.web.api.controller.locale;

import net.ontrack.core.RunProfile;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@Profile(RunProfile.PROD)
public class ProductionAPILocalisationController extends APILocalisationController {
	
	private static final long CACHE_PERIOD_SECONDS = 365 * 24 * 3600L;
	
	private final Map<Locale, String> contents = new ConcurrentHashMap<>();

	@Autowired
	public ProductionAPILocalisationController(Strings strings) {
		super(strings);
	}
	
	/**
	 * Preloads all the versions
	 */
	@PostConstruct
	public void init() {
		// Gets all the locales
		Collection<Locale> locales = strings.getSupportedLocales().getSupportedLocales();
		for (Locale locale : locales) {
			contents.put(locale, generateJS(locale));
		}
	}

	/**
	 * All contents have already been generated at start-up. They are served
	 * only once and cache parameters are set to the response.
	 * 
	 * @param version
	 *            This parameter prevents the caching from one version to the
	 *            other
	 */
	@Override
	@RequestMapping(value = "/api/localization/{language}/{version:.*}", method = RequestMethod.GET)
	public void localisation(Locale locale, HttpServletResponse response, @PathVariable String language, @PathVariable String version) throws IOException {
		
		if (locale == null) {
			locale = Locale.ENGLISH;
		}
		
		locale = strings.getSupportedLocales().filterForLookup(locale);
		
		String content = contents.get(locale);
		if (content == null) {
			throw new IOException("Could not find any content for locale " + locale);
		}
		
		// See https://developers.google.com/speed/docs/best-practices/caching for cache management
		final long now = System.currentTimeMillis();
		response.setDateHeader("Expires", now + CACHE_PERIOD_SECONDS * 1000L);
		response.setDateHeader("Last-Modified", now - 3600 * 1000);
		
		writeJS(content, response);
	}

}
