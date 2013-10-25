package net.ontrack.client.support;


public class ClientGeneralException extends ClientMessageException {

    public ClientGeneralException(Object request, Exception ex) {
        super(ex, String.format("Error while executing %s: %s", request, ex));
    }

}
