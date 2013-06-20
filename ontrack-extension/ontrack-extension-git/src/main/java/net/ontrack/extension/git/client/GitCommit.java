package net.ontrack.extension.git.client;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class GitCommit {

    private final String id;
    private final GitPerson author;
    private final GitPerson committer;
    private final DateTime commitTime;
    private final String fullMessage;
    private final String shortMessage;
}
