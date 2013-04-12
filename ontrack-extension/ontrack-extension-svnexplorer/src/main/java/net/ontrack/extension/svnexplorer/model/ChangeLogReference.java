package net.ontrack.extension.svnexplorer.model;

import lombok.Data;

@Data
public class ChangeLogReference {

    private final String path;
    private final long start;
    private final long end;

    public boolean isNone() {
        return start == end;
    }
}
