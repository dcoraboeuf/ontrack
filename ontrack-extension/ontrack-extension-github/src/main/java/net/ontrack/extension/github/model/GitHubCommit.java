package net.ontrack.extension.github.model;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class GitHubCommit {

    private final String id;
    private final String author;
    private final DateTime commitTime;
    private final String message;

}
