package net.ontrack.extension.svn.service.model;

import lombok.Data;

@Data
public class SVNRepositoryForm {

    private final String name;
    private final String url;
    private final String user;
    private final String password;
    private final String branchPattern;
    private final String tagPattern;
    private final String tagFilterPattern;
    private final String browserForPath;
    private final String browserForRevision;
    private final String browserForChange;
    private final int indexationInterval;
    private final long indexationStart;

}
