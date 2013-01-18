package net.ontrack.core.support;

import net.sf.jstring.support.CoreException;

public abstract class InputException extends CoreException {

	private static final long serialVersionUID = 1L;

	public InputException(Object... params) {
		super(params);
	}

}
