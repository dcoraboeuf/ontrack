package net.ontrack.web.api.controller.locale;

import net.ontrack.core.RunProfile;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Controller
@Profile({RunProfile.TEST, RunProfile.IT, RunProfile.DEV})
public class ReloadableAPILocalisationController extends APILocalisationController {

	@Autowired
	public ReloadableAPILocalisationController(Strings strings) {
		super(strings);
	}

	/**
	 * Regenerates the content each time. It is slower but allows for regeneration at runtime.
	 * @param version This parameter prevents the caching from one version to the other
	 */
	@Override
	@RequestMapping(value = "/api/localization/{language}/{version:.*}", method = RequestMethod.GET)
	public void localisation(Locale locale, HttpServletResponse response, @PathVariable String language, @PathVariable String version) throws IOException {
		
		if (locale == null) {
			locale = Locale.ENGLISH;
		}
		
		String content = generateJS(locale);
		
		writeJS(content, response);
	}

}
