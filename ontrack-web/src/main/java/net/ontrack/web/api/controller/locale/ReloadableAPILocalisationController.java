package net.ontrack.web.api.controller.locale;

import net.ontrack.core.RunProfile;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

@Controller
@Profile({RunProfile.TEST, RunProfile.IT, RunProfile.DEV})
public class ReloadableAPILocalisationController extends APILocalisationController {

    @Autowired
    public ReloadableAPILocalisationController(Strings strings) {
        super(strings);
    }

    /**
     * Regenerates the content each time. It is slower but allows for regeneration at runtime.
     *
     * @param version This parameter prevents the caching from one version to the other
     */
    @Override
    @RequestMapping(value = "/api/localization/{language}/{version:.*}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> localisation(@PathVariable String language, @PathVariable String version) throws IOException {
        return new ResponseEntity<>(
                generateLocalizationMap(new Locale(language)),
                HttpStatus.OK
        );
    }

}
