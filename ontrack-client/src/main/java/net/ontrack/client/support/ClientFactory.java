package net.ontrack.client.support;

import net.ontrack.client.ControlUIClient;
import net.ontrack.client.ManageUIClient;

public class ClientFactory {

    private final String url;

    private ClientFactory(String url) {
        this.url = url;
    }

    public static ClientFactory create(String url) {
        return new ClientFactory(url);
    }

    public ControlUIClient control() {
        return new DefaultControlUIClient(url);
    }

    public ManageUIClient manage() {
        return new DefaultManageUIClient(url);
    }

}
