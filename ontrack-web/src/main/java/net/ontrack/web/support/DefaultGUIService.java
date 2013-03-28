package net.ontrack.web.support;

import net.ontrack.service.AdminService;
import net.ontrack.service.GUIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultGUIService implements GUIService {

    private final AdminService adminService;

    @Autowired
    public DefaultGUIService(AdminService adminService) {
        this.adminService = adminService;
    }

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
        return adminService.getGeneralConfiguration().getBaseUrl();
    }
}
