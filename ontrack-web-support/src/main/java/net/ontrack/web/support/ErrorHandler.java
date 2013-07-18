package net.ontrack.web.support;

import net.sf.jstring.support.CoreException;

import java.util.Locale;

public interface ErrorHandler {

    ErrorMessage handleError(Locale locale, Exception ex);

    String displayableError(CoreException ex, Locale locale);

}
