package net.ontrack.client.support;


public class ClientMessageException extends ClientException {

    private final String message;

    public ClientMessageException(String content) {
        super(content);
        this.message = content;
    }

    @Override
    public String getMessage() {
        return message;
    }

}