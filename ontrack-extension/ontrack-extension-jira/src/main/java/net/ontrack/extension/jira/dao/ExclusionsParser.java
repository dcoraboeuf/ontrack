package net.ontrack.extension.jira.dao;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class ExclusionsParser {
    private final String exclusions;
    private Set<String> excludedProjects;
    private Set<String> excludedIssues;

    public ExclusionsParser(String exclusions) {
        this.exclusions = exclusions;
    }

    public Set<String> getExcludedProjects() {
        return excludedProjects;
    }

    public Set<String> getExcludedIssues() {
        return excludedIssues;
    }

    public ExclusionsParser invoke() {
        excludedProjects = new HashSet<>();
        excludedIssues = new HashSet<>();
        String[] tokens = StringUtils.split(exclusions, ",");
        if (tokens != null) {
            for (String token : tokens) {
                int index = token.indexOf("-");
                if (index > 0) {
                    excludedIssues.add(token);
                } else {
                    excludedProjects.add(token);
                }
            }
        }
        return this;
    }
}
