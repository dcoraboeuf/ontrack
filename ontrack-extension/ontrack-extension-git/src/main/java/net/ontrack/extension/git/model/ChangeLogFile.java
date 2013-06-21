package net.ontrack.extension.git.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.ontrack.extension.git.client.GitChangeType;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeLogFile {

    private final GitChangeType changeType;
    private final String path;
    private final String path2;
    private final String url;

    public static ChangeLogFile of(GitChangeType changeType, String path) {
        return new ChangeLogFile(changeType, path, null, "");
    }

    public static ChangeLogFile of(GitChangeType changeType, String path, String path2) {
        return new ChangeLogFile(changeType, path, path2, "");
    }
}
