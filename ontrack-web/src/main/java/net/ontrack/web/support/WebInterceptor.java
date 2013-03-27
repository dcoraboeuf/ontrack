package net.ontrack.web.support;

import net.ontrack.core.model.UserMessage;
import net.sf.jstring.Strings;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WebInterceptor extends HandlerInterceptorAdapter {

	private static final ThreadLocal<HttpServletRequest> CURRENT_REQUEST = new ThreadLocal<HttpServletRequest>();
    private final Strings strings;

    public WebInterceptor(Strings strings) {
        this.strings = strings;
    }

    public static HttpServletRequest getCurrentRequest() {
		HttpServletRequest request = CURRENT_REQUEST.get();
		if (request == null) {
			throw new IllegalStateException("No current request");
		} else {
			return request;
		}
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		CURRENT_REQUEST.set(request);
		// OK
		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler,
			ModelAndView modelAndView) throws Exception {
		// Adds the base URL to the model
		if (modelAndView != null) {
            // Gets the locale
            Locale locale = RequestContextUtils.getLocale(request);
            // Adds the base URL to the model
			modelAndView.addObject("baseURL", WebUtils.getBaseURL(request));
            // Gets all user messages
            Map<String, Alert> alerts = new HashMap<>();
            for (Map.Entry<String, Object> entry : modelAndView.getModel().entrySet()) {
                Object value = entry.getValue();
                if (value instanceof UserMessage) {
                    UserMessage message = (UserMessage) value;
                    Alert alert = toAlert(locale, message);
                    alerts.put(entry.getKey(), alert);
                }
            }
            // Replaces the messages by alerts
            for (Map.Entry<String, Alert> entry : alerts.entrySet()) {
                modelAndView.addObject(entry.getKey(), entry.getValue());
            }
		}
		// OK
		super.postHandle(request, response, handler, modelAndView);
	}

    private Alert toAlert(Locale locale, UserMessage message) {
        return new Alert(message.getType(), message.getMessage().getLocalizedMessage(strings, locale));
    }

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		CURRENT_REQUEST.set(null);
	}

}
