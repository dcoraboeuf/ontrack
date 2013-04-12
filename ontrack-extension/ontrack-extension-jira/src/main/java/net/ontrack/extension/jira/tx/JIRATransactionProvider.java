package net.ontrack.extension.jira.tx;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;
import net.ontrack.extension.jira.JIRAConfigurationExtension;
import net.ontrack.tx.TransactionResource;
import net.ontrack.tx.TransactionResourceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class JIRATransactionProvider implements TransactionResourceProvider<JIRASession> {

    private final JIRAConfigurationExtension configurationExtension;

    @Autowired
    public JIRATransactionProvider(JIRAConfigurationExtension configurationExtension) {
        this.configurationExtension = configurationExtension;
    }

    @Override
    public JIRASession createTxResource() {
        String url = configurationExtension.getUrl();
        String user = configurationExtension.getUser();
        String password = configurationExtension.getPassword();
        JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
        try {
            URI jiraServerUri = new URI(url);
            JiraRestClient client = factory.createWithBasicHttpAuthentication(jiraServerUri, user, password);
            return new DefaultJIRASession(client);
        } catch (URISyntaxException ex) {
            throw new JIRAConnectionException (url, ex);
        }
    }

    @Override
    public boolean supports(Class<? extends TransactionResource> resourceType) {
        return JIRASession.class.isAssignableFrom(resourceType);
    }
}
