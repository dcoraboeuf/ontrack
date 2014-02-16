package net.ontrack.extension.issue;

/**
 * Defines a generic service used to access issues from a ticketing system like
 * JIRA, GitHub, etc.
 */
public interface IssueService {

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

}
