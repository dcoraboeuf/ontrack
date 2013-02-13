package net.ontrack.client.support;

import net.ontrack.client.ControlUIClient;

public class ControlUIClientFactory {

    private final String url;

    private ControlUIClientFactory(String url) {
        this.url = url;
    }

    public ControlUIClient build() {
        return new DefaultControlUIClient(url);
    }

    public static ControlUIClientFactory create(String url) {
        return new ControlUIClientFactory(url);
    }

}
