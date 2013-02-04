package net.ontrack.backend;

import net.ontrack.core.support.InputException;

public class ImageIncorrectMIMETypeException extends InputException {
	
	public ImageIncorrectMIMETypeException(String actual, String expected) {
		super(actual, expected);
	}

}
