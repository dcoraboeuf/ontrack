package net.ontrack.extension.svnexplorer.model;

import lombok.Data;
import net.ontrack.extension.svn.service.model.SVNRepository;

import java.util.List;

@Data
public class ChangeLogIssues {

    private final String allIssuesLink;
    private final SVNRepository repository;
    private final List<ChangeLogIssue> list;

}
