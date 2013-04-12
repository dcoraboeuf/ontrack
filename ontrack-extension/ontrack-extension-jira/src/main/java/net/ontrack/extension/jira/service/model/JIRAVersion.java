package net.ontrack.extension.jira.service.model;

import lombok.Data;

@Data
public class JIRAVersion {

    private final String name;
    private final boolean released;

}
