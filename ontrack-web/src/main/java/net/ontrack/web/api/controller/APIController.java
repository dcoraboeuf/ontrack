package net.ontrack.web.api.controller;

import net.ontrack.core.support.InputException;
import net.ontrack.core.support.NotFoundException;
import net.ontrack.web.api.model.Resource;
import net.ontrack.web.support.ErrorHandler;
import net.ontrack.web.support.ErrorMessage;
import net.sf.jstring.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

public abstract class APIController {

    protected final ErrorHandler errorHandler;
    protected final Strings strings;

    protected APIController(ErrorHandler errorHandler, Strings strings) {
        this.errorHandler = errorHandler;
        this.strings = strings;
    }

    protected <T extends Resource> ResponseEntity<T> ok(T resource) {
        return new ResponseEntity<>(
                resource,
                HttpStatus.OK
        );
    }

    protected ResponseEntity<String> getMessageResponse(String message, HttpStatus status) {
        // Header
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/plain; charset=utf-8");
        // OK
        return new ResponseEntity<>(message, responseHeaders, status);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> onNotFoundException(Locale locale, NotFoundException ex) {
        // Returns a message to display to the user
        String message = ex.getLocalizedMessage(strings, locale);
        // OK
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InputException.class)
    public ResponseEntity<String> onInputException(Locale locale, InputException ex) {
        // Returns a message to display to the user
        String message = ex.getLocalizedMessage(strings, locale);
        // OK
        return getMessageResponse(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> onException(Locale locale, Exception ex) throws Exception {
        // Ignores access errors
        if (ex instanceof AccessDeniedException) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Error message
        ErrorMessage error = errorHandler.handleError(locale, ex);
        // Returns a message to display to the user
        String message = strings.get(locale, "general.error.full", error.getMessage(), error.getUuid());
        // Ok
        return getMessageResponse(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
