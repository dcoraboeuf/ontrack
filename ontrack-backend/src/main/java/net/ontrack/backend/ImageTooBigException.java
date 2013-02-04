package net.ontrack.backend;

import net.ontrack.core.support.InputException;

public class ImageTooBigException extends InputException {
	
	public ImageTooBigException(long actual, long maximum) {
		super(actual, maximum);
	}

}
