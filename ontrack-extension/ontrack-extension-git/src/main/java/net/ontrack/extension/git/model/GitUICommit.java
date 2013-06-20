package net.ontrack.extension.git.model;

import lombok.Data;
import net.ontrack.extension.git.client.GitCommit;

@Data
public class GitUICommit {

    private final GitCommit commit;
    private final String link;
    private final String elapsedTime;
    private final String formattedTime;

}
