package net.ontrack.web.hateoas;

import net.ontrack.core.support.InputException;
import net.ontrack.core.support.NotFoundException;
import net.sf.jstring.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

public abstract class AbstractResourceController {

    private final Strings strings;

    protected AbstractResourceController(Strings strings) {
        this.strings = strings;
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> onNotFoundException(Locale locale, InputException ex) {
        // Returns a message to display to the user
        String message = ex.getLocalizedMessage(strings, locale);
        // OK
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
}
