package net.ontrack.extension.jira.service.model;

import lombok.Data;

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
}
