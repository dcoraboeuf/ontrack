package net.ontrack.extension.jira.model;

import lombok.Data;

@Data
public class JIRAConfiguration {
    private final int id;
    private final String name;
    private final String url;
    private final String user;
    private final String password;
    private final String exclusions;
}
