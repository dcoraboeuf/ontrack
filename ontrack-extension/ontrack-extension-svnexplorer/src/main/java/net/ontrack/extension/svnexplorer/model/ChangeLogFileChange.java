package net.ontrack.extension.svnexplorer.model;

import lombok.Data;
import net.ontrack.extension.svn.service.model.SVNRevisionInfo;

@Data
public class ChangeLogFileChange {

    private final SVNRevisionInfo revisionInfo;
    private final String changeType;
    private final String url;

}
