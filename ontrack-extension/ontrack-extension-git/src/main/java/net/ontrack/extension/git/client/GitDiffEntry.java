package net.ontrack.extension.git.client;

import lombok.Data;

@Data
public class GitDiffEntry {

    private final GitChangeType changeType;
    private final String oldPath;
    private final String newPath;

}
