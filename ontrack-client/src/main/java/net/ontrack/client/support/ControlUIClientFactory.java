package net.ontrack.client.support;

import net.ontrack.client.ControlUIClient;
import net.ontrack.client.ManageUIClient;

public class ControlUIClientFactory {

    private final String url;

    private ControlUIClientFactory(String url) {
        this.url = url;
    }

    public static ControlUIClientFactory create(String url) {
        return new ControlUIClientFactory(url);
    }

    public ControlUIClient control() {
        return new DefaultControlUIClient(url);
    }

    public ManageUIClient manage() {
        return new DefaultManageUIClient(url);
    }

}
