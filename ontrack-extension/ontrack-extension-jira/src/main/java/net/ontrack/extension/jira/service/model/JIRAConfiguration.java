package net.ontrack.extension.jira.service.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

@Data
public class JIRAConfiguration {
    private final int id;
    private final String name;
    private final String url;
    private final String user;
    private final String password;
    private final Set<String> excludedProjects;
    private final Set<String> excludedIssues;

    /**
     * Gets the URL to a page that displays an issue.
     *
     * @param issue Key of the issue to display (like XXX-111)
     * @return URL to the page
     */
    public String getIssueURL(String issue) {
        String base = getUrl();
        if (StringUtils.isNotBlank(base)) {
            return String.format("%s/browse/%s", base, issue);
        } else {
            return issue;
        }
    }
}
