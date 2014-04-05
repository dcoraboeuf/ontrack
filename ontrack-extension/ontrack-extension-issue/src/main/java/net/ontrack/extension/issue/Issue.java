package net.ontrack.extension.issue;

import org.joda.time.DateTime;

/**
 * Abstract definition of an issue.
 */
public interface Issue {

    String getKey();

    String getSummary();

    IssueStatus getStatus();

    DateTime getUpdateTime();

}
