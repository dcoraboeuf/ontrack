package net.ontrack.web.support;

import net.sf.jstring.Strings;
import net.sf.jstring.support.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
public class DefaultErrorHandler implements ErrorHandler {

    private final Logger logger = LoggerFactory.getLogger("User");
    private final Strings strings;

    @Autowired
    public DefaultErrorHandler(Strings strings) {
        this.strings = strings;
    }

    @Override
    public ErrorMessage handleError(Locale locale, Exception ex) {
        // Generates a UUID
        String uuid = UUID.randomUUID().toString();
        // Error message
        String displayMessage;
        String loggedMessage;
        boolean stackTrace;
        if (ex instanceof CoreException) {
            loggedMessage = ((CoreException) ex).getLocalizedMessage(strings, Locale.ENGLISH);
            stackTrace = false;
            displayMessage = ((CoreException) ex).getLocalizedMessage(strings, locale);
        } else {
            loggedMessage = ex.getMessage();
            stackTrace = true;
            // Gets a display message for this exception class
            String messageKey = ex.getClass().getName();
            if (strings.isDefined(locale, messageKey)) {
                displayMessage = strings.get(locale, messageKey, false);
            } else {
                displayMessage = strings.get(locale, "general.error.technical");
            }
        }
        // Traces the error
        // TODO Adds request information
        // TODO Adds authentication information
        String formattedLoggedMessage = String.format("[%s] %s", uuid, loggedMessage);
        if (stackTrace) {
            logger.error(formattedLoggedMessage, ex);
        } else {
            logger.error(formattedLoggedMessage);
        }
        // OK
        return new ErrorMessage(uuid, displayMessage);
    }

    @Override
    public String displayableError(CoreException ex, Locale locale) {
        return ex.getLocalizedMessage(strings, locale);
    }

}
