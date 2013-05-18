package net.ontrack.client.support;

import net.sf.jstring.support.CoreException;

public class ClientException extends CoreException {

    public ClientException(Object... params) {
        super(params);
    }

    public ClientException(Throwable error, Object... params) {
        super(error, params);
    }
}
