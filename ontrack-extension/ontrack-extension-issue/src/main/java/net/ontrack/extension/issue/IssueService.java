package net.ontrack.extension.issue;

import com.google.common.base.Function;

import java.util.Collection;

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
     * issue services.
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
}
