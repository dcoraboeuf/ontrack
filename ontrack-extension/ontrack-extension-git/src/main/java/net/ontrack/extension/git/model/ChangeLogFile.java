package net.ontrack.extension.git.model;

import lombok.Data;
import net.ontrack.extension.git.client.GitDiffEntry;

@Data
public class ChangeLogFile {

    private final GitDiffEntry diff;
    private final String url;

}
