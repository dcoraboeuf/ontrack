package net.ontrack.extension.jira.tx;

import com.atlassian.jira.rest.client.JiraRestClient;
import net.ontrack.tx.TransactionResource;

public interface JIRASession extends TransactionResource {

    JiraRestClient getClient();

}
