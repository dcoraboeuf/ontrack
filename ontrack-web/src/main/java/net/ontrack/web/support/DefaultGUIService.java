package net.ontrack.web.support;

import net.ontrack.core.RunProfile;
import net.ontrack.service.GUIService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({RunProfile.IT, RunProfile.DEV, RunProfile.PROD})
public class DefaultGUIService implements GUIService {

    @Override
    public String toGUI(String uri) {
        return toURL("gui/" + uri);
    }

    private String toURL(String uri) {
        return getBaseURL() + uri;
    }

    /**
     * Since this method can be called from a batch, the base URL
     * cannot be deducted from a HTTP request. It must therefore
     * be a configuration parameter.
     */
    public String getBaseURL() {
        // FIXME Returns a correct URL
        return "http://localhost:8080/";
    }
}
