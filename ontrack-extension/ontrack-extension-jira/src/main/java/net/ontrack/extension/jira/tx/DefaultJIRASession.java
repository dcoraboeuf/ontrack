package net.ontrack.extension.jira.tx;

import com.atlassian.jira.rest.client.api.JiraRestClient;

public class DefaultJIRASession implements JIRASession {

    private final JiraRestClient client;

    public DefaultJIRASession(JiraRestClient client) {
        this.client = client;
    }

    @Override
    public JiraRestClient getClient() {
        return client;
    }

    /**
     * No closing needed.
     */
    @Override
    public void close() {
    }
}
