package net.ontrack.extension.jira.tx;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import net.ontrack.extension.jira.JIRAConfigurationService;
import net.ontrack.extension.jira.service.model.JIRAConfiguration;
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
        JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        try {
            URI jiraServerUri = new URI(url);
            JiraRestClient client = factory.createWithBasicHttpAuthentication(jiraServerUri, user, password);
            return new DefaultJIRASession(client);
        } catch (URISyntaxException ex) {
            throw new JIRAConnectionException(url, ex);
        }
    }
}
