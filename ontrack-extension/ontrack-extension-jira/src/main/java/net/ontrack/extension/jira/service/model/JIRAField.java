package net.ontrack.extension.jira.service.model;

import lombok.Data;

@Data
public class JIRAField {

    private final String name;
    private final String type;
    private final String value;

}
