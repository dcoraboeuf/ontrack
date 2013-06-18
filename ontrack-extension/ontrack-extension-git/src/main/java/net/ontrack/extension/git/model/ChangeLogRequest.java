package net.ontrack.extension.git.model;

import lombok.Data;

@Data
public class ChangeLogRequest {

    private final String project;
    private final String branch;
    private final String from;
    private final String to;

}
