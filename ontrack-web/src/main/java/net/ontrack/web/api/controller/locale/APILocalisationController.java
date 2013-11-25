package net.ontrack.web.api.controller.locale;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import net.ontrack.web.api.model.Name;
import net.sf.jstring.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

public abstract class APILocalisationController {

    protected final Strings strings;

    public APILocalisationController(Strings strings) {
        this.strings = strings;
    }

    public abstract ResponseEntity<Map<String, String>> localisation(String language, String version) throws IOException;

    @RequestMapping("/api/languages")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Collection<Name> localisationList() {
        return Collections2.transform(
                strings.getSupportedLocales().getSupportedLocales(),
                new Function<Locale, Name>() {
                    @Override
                    public Name apply(Locale o) {
                        return new Name(o.toString(), "language." + o);
                    }
                }
        );
    }

    protected Map<String, String> generateLocalizationMap(Locale locale) {
        // Restricts the locale
        locale = strings.getSupportedLocales().filterForLookup(locale);

        // Gets the list of key/values
        return strings.getKeyValues(locale);
    }


}
