package net.ontrack.web.support;

import net.sf.jstring.support.CoreException;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public interface ErrorHandler {

	ErrorMessage handleError(HttpServletRequest request, Locale locale, Exception ex);

	String displayableError(CoreException ex, Locale locale);

}
