package net.ontrack.extension.jira.tx;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.jira.JIRAConfigurationService;
import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;

@Component
public class DefaultJIRASessionFactory implements JIRASessionFactory {

    private final JIRAConfigurationService jiraConfigurationService;
    private final SecurityUtils securityUtils;

    @Autowired
    public DefaultJIRASessionFactory(JIRAConfigurationService jiraConfigurationService, SecurityUtils securityUtils) {
        this.jiraConfigurationService = jiraConfigurationService;
        this.securityUtils = securityUtils;
    }

    @Override
    public JIRASession create(final JIRAConfiguration configuration) {
        String url = configuration.getUrl();
        String user = configuration.getUser();
        // Password used locally only
        String password = securityUtils.asAdmin(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return jiraConfigurationService.getPassword(configuration.getId());
            }
        });
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
