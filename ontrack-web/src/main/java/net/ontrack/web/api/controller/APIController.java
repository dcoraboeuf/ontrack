package net.ontrack.web.api.controller;

import net.ontrack.core.support.InputException;
import net.ontrack.core.support.NotFoundException;
import net.ontrack.web.support.ErrorHandler;
import net.ontrack.web.support.ErrorMessage;
import net.sf.jstring.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.Locale;

public abstract class APIController {

    protected final ErrorHandler errorHandler;
    protected final Strings strings;

    protected APIController(ErrorHandler errorHandler, Strings strings) {
        this.errorHandler = errorHandler;
        this.strings = strings;
    }

    protected ResponseEntity<byte[]> image(byte[] content, byte[] defaultImage) {
        if (content == null) {
            if (defaultImage != null) {
                content = defaultImage;
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }
        try {
            return renderImage(content);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    protected ResponseEntity<byte[]> renderImage(byte[] content) throws IOException {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.IMAGE_PNG);
        // See https://developers.google.com/speed/docs/best-practices/caching for cache management
        // Just sets the last modified time to now
        final long now = System.currentTimeMillis();
        responseHeaders.setExpires(now + 24 * 3600L * 1000L); // Expires in one day
        responseHeaders.setLastModified(now - 3600 * 1000); // Modified one hour ago
        // OK
        return new ResponseEntity<>(content, responseHeaders, HttpStatus.OK);
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
