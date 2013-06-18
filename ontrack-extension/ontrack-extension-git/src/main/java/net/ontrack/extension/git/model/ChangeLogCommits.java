package net.ontrack.extension.git.model;

import lombok.Data;
import net.ontrack.extension.git.client.GitCommit;

import java.util.List;

@Data
public class ChangeLogCommits {

    private final List<GitCommit> commits;

}
