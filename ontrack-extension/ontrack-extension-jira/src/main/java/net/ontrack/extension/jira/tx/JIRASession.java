package net.ontrack.extension.jira.tx;


import com.atlassian.jira.rest.client.api.JiraRestClient;
import net.ontrack.tx.TransactionResource;

public interface JIRASession extends TransactionResource {

    JiraRestClient getClient();

}
