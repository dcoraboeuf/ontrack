package net.ontrack.extension.jira.service.model;

import lombok.Data;
import net.ontrack.extension.issue.IssueServiceConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.regex.Pattern;

// FIXME The password must be removed, and the update service must be updated
// in order to update the password only if filled in
@Data
public class JIRAConfiguration implements IssueServiceConfig {

    public static final Pattern ISSUE_PATTERN = Pattern.compile("[A-Za-z][A-Za-z0-9]*\\-[0-9]+");

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

    public boolean isIssue(String token) {
        return ISSUE_PATTERN.matcher(token).matches()
                && !isIssueExcluded(token);
    }

    private boolean isIssueExcluded(String token) {
        return excludedIssues.contains(token)
                || excludedProjects.contains(StringUtils.substringBefore(token, "-"));
    }
}
