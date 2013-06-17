package net.ontrack.extension.git.model;

import lombok.Data;

@Data
public class GitConfiguration {

    private final String remote;
    private final String branch;
    private final String tag;

}
