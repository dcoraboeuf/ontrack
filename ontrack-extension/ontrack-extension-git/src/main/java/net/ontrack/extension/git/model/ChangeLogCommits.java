package net.ontrack.extension.git.model;

import lombok.Data;
import net.ontrack.extension.git.client.GitLog;

@Data
public class ChangeLogCommits {

    private final GitLog log;

}
