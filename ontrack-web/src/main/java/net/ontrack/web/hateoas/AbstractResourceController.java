package net.ontrack.web.hateoas;

import net.ontrack.core.support.InputException;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Locale;

public abstract class AbstractResourceController extends AbstractUIController {

    public AbstractResourceController(ErrorHandler errorHandler, Strings strings) {
        super(errorHandler, strings);
    }

    @Override
    public ResponseEntity<String> onInputException(Locale locale, InputException ex) {
        ResponseEntity<String> response = super.onInputException(locale, ex);
        return new ResponseEntity<>(response.getBody(), HttpStatus.BAD_REQUEST);
    }
}
