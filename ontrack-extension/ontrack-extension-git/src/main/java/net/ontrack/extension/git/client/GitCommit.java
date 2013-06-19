package net.ontrack.extension.git.client;

import lombok.Data;
import org.joda.time.DateTime;

import java.util.Collection;

@Data
public class GitCommit {

    private final String id;
    private final GitPerson author;
    private final GitPerson committer;
    private final DateTime commitTime;
    private final String fullMessage;
    private final String shortMessage;
    private final Collection<String> parents;
}
