package net.ontrack.web.support;

import net.ontrack.core.model.UserMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

public class WebUtils {

	public static final String ITEACH_URL = "iteach.url";

	public static String getBaseURL(HttpServletRequest request) {
		String systemDefinedURL = System.getProperty(ITEACH_URL);
		if (StringUtils.isNotBlank(systemDefinedURL)) {
			return systemDefinedURL;
		} else {
			return computeBaseURL(request);
		}
	}

	public static String computeBaseURL(HttpServletRequest request) {
		StringBuilder s = new StringBuilder();
		// Scheme
		s.append(request.getScheme()).append("://");
		// Host
		s.append(request.getServerName());
		// Port
		int port = request.getServerPort();
		if (port != 80) {
			s.append(":").append(port);
		}
		// Context path
		String path = request.getContextPath();
		if (StringUtils.isNotBlank(path)) {
			s.append(path);
		}
		// OK
		return s.toString();
	}

    public static void userMessage(RedirectAttributes redirectAttributes, UserMessage message) {
        redirectAttributes.addFlashAttribute("message", message);
    }
}
