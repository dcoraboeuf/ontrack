package net.ontrack.web.api.controller.locale;

import net.sf.jstring.Strings;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public abstract class APILocalisationController {

    protected final Strings strings;

    public APILocalisationController(Strings strings) {
        this.strings = strings;
    }

    public abstract ResponseEntity<Map<String, String>> localisation(String language, String version) throws IOException;

    protected Map<String, String> generateLocalizationMap(Locale locale) {
        // Restricts the locale
        locale = strings.getSupportedLocales().filterForLookup(locale);

        // Gets the list of key/values
        return strings.getKeyValues(locale);
    }


}
