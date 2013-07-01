package net.ontrack.extension.svnexplorer.model;

import lombok.Data;

import java.util.List;

@Data
public class ChangeLogIssues {

    private final String jiraUrl;
    private final List<ChangeLogIssue> list;

}
