package net.ontrack.extension.jira.service.model;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class JIRAIssue {

    private final String url;
    private final String key;
    private final String summary;
    private final String assignee;
    private final DateTime updateTime;

}
