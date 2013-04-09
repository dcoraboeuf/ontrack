package net.ontrack.backend;

import net.ontrack.core.RunProfile;
import net.ontrack.service.GUIService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(RunProfile.TEST)
public class MockGUIService implements GUIService {
    @Override
    public String toGUI(String uri) {
        return "http://test/gui/" + uri;
    }
}
