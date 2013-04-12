package net.ontrack.extension.svnexplorer.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChangeLogFile {

    private final String path;
    private final String url;
    private final List<ChangeLogFileChange> changes = new ArrayList<>();

    public ChangeLogFile addChange(ChangeLogFileChange change) {
        changes.add(change);
        return this;
    }

}
