package net.ontrack.extension.issue;

import com.google.common.base.Function;

/**
 * Configuration for an issue service.
 */
public interface IssueServiceConfig {

    Function<IssueServiceConfig, IssueServiceConfigSummary> summaryFn = new Function<IssueServiceConfig, IssueServiceConfigSummary>() {
        @Override
        public IssueServiceConfigSummary apply(IssueServiceConfig config) {
            if (config != null) {
                return new IssueServiceConfigSummary(
                        config.getId(),
                        config.getName()
                );
            } else {
                return null;
            }
        }
    };

    /**
     * Technical ID for this configuration
     */
    int getId();

    /**
     * Display name for this configuration
     */
    String getName();

}
