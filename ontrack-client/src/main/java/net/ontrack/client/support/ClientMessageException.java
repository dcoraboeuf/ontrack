package net.ontrack.client.support;

import net.sf.jstring.support.CoreException;

public class ClientMessageException extends CoreException {

    public ClientMessageException(String content) {
        super(content);
    }
}
