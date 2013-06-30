package net.ontrack.extension.github.model;

import lombok.Data;

@Data
public class GitHubCommit {

    private final String id;
    private final String author;
    private final String shortMessage;

}
