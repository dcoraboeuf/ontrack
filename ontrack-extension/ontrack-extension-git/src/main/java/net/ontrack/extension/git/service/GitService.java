package net.ontrack.extension.git.service;

import net.ontrack.extension.git.model.ChangeLogCommits;
import net.ontrack.extension.git.model.ChangeLogFiles;
import net.ontrack.extension.git.model.ChangeLogSummary;
import net.ontrack.extension.git.model.GitImportBuildsForm;

import java.util.Locale;

public interface GitService {

    void importBuilds(int branchId, GitImportBuildsForm form);

    ChangeLogSummary getChangeLogSummary(Locale locale, int branchId, int buildFromId, int buildToId);

    ChangeLogCommits getChangeLogCommits(Locale locale, ChangeLogSummary summary);

    ChangeLogFiles getChangeLogFiles(Locale locale, ChangeLogSummary summary);
}
