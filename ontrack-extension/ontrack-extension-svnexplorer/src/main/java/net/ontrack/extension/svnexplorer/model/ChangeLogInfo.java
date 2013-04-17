package net.ontrack.extension.svnexplorer.model;

import lombok.Data;

import java.util.List;

@Data
public class ChangeLogInfo {

    private final List<ChangeLogInfoStatus> statuses;
    private final List<ChangeLogFile> sensibleFiles;

}
