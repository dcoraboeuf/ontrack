package net.ontrack.extension.issue;

import com.google.common.base.Function;

import java.util.Collection;
import java.util.Set;

/**
 * Defines a generic service used to access issues from a ticketing system like
 * JIRA, GitHub, etc.
 */
public interface IssueService {

    Function<IssueService, IssueServiceSummary> summaryFn = new Function<IssueService, IssueServiceSummary>() {
        @Override
        public IssueServiceSummary apply(IssueService service) {
            if (service != null) {
                return new IssueServiceSummary(
                        service.getId(),
                        service.getName()
                );
            } else {
                return null;
            }
        }
    };

    /**
     * Gets the ID of this service. It must be unique among all the available
     * issue services. This must be mapped to the associated extension since this
     * ID can be used to identify web resources using the <code>/extension/&lt;id&gt;</code> URI.
     */
    String getId();

    /**
     * Gets the display name for this service.
     */
    String getName();

    /**
     * Is this service currently enabled in `ontrack`?
     */
    boolean isEnabled();

    /**
     * Gets a configuration to be used with this service, using its ID.
     */
    IssueServiceConfig getConfigurationById(int id);

    /**
     * Gets the list of all available configurations
     */
    Collection<IssueServiceConfigSummary> getAllConfigurations();

    /**
     * Given a message, extracts the issue keys from the message
     *
     * @param issueServiceConfig Configuration for the service
     * @param message            Message to scan
     * @return List of keys (can be empty, never <code>null</code>)
     */
    Set<String> extractIssueKeysFromMessage(IssueServiceConfig issueServiceConfig, String message);

    /**
     * Given a message, returns this message where issues have been replaced
     * by hyperlinks to a page that can display details about this issue.
     *
     * @param issueServiceConfig Configuration for the service
     * @param message            Message to format
     * @return Formatted message
     */
    String formatIssuesInMessage(IssueServiceConfig issueServiceConfig, String message);

    /**
     * Given a key, tries to find the issue with this key.
     *
     * @param issueServiceConfig Configuration for the service
     * @param key                Issue key
     * @return Issue if found, <code>null</code> otherwise
     */
    Issue getIssue(IssueServiceConfig issueServiceConfig, String key);

    /**
     * Given a list of issues, returns a link that allows the user to display the list of
     * all those issues in a browser.
     *
     * @param issueServiceConfig Configuration for the service
     * @param issues             List of issues to display. Can be empty, but not <code>null</code>.
     * @return Link
     */
    String getLinkForAllIssues(IssueServiceConfig issueServiceConfig, Collection<Issue> issues);

    /**
     * Quick test about the validity of any token to be an issue key.
     *
     * @param token Token to test (may be <code>null</code>)
     * @return <code>true</code> if the <code>token</code> may represent a valid issue key for this issue service. If
     *         this method returns <code>true</code>, it does not mean that this is actually a valid issue.
     */
    boolean isIssue(String token);
}
