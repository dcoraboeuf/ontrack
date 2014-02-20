package net.ontrack.extension.jira.tx;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import net.ontrack.core.model.Ack;
import net.ontrack.extension.jira.JIRAConfigurationService;
import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import net.ontrack.extension.jira.service.model.JIRAConfigurationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class DefaultJIRASessionFactory implements JIRASessionFactory {

    private final JIRAConfigurationService jiraConfigurationService;

    @Autowired
    public DefaultJIRASessionFactory(JIRAConfigurationService jiraConfigurationService) {
        this.jiraConfigurationService = jiraConfigurationService;
    }

    @Override
    public JIRASession create(JIRAConfiguration configuration) {
        String url = configuration.getUrl();
        String user = configuration.getUser();
        String password = jiraConfigurationService.getPassword(configuration.getId());
        JiraRestClient client = createJiraRestClient(url, user, password);
        return new DefaultJIRASession(client);
    }

    private JiraRestClient createJiraRestClient(String url, String user, String password) {
        JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        JiraRestClient client;
        try {
            URI jiraServerUri = new URI(url);
            client = factory.createWithBasicHttpAuthentication(jiraServerUri, user, password);
        } catch (URISyntaxException ex) {
            throw new JIRAConnectionException(url, ex);
        }
        return client;
    }

    @Override
    public Ack testConfiguration(JIRAConfigurationForm form) {
        // Tries to create the JIRA client
        JiraRestClient client = createJiraRestClient(form.getUrl(), form.getUser(), form.getPassword());
        // Tries anything
        client.getMetadataClient().getServerInfo().claim();
        // If no exception, everything is fine
        return Ack.OK;
    }
}
