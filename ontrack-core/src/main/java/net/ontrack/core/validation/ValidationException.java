package net.ontrack.core.validation;

import net.ontrack.core.support.InputException;
import net.sf.jstring.MultiLocalizable;

public class ValidationException extends InputException {

	public ValidationException(MultiLocalizable multiLocalizable) {
		super(multiLocalizable);
	}

}
