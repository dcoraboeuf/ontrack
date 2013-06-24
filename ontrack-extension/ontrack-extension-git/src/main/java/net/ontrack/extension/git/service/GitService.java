package net.ontrack.extension.git.service;

import net.ontrack.extension.git.model.*;

import java.util.Locale;

public interface GitService {

    GitConfiguration getGitConfiguration(int branchId);

    void importBuilds(int branchId, GitImportBuildsForm form);

    ChangeLogSummary getChangeLogSummary(Locale locale, int branchId, int buildFromId, int buildToId);

    ChangeLogCommits getChangeLogCommits(Locale locale, ChangeLogSummary summary);

    ChangeLogFiles getChangeLogFiles(Locale locale, ChangeLogSummary summary);
}
