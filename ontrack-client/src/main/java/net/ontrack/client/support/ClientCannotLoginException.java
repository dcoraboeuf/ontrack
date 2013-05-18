package net.ontrack.client.support;

import net.sf.jstring.support.CoreException;
import org.apache.commons.lang3.ObjectUtils;

public class ClientCannotLoginException extends ClientException {

	public ClientCannotLoginException(Object request) {
		super(ObjectUtils.toString(request, ""));
	}

}
