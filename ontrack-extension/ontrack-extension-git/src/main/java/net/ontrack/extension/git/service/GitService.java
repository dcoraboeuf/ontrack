package net.ontrack.extension.git.service;

import net.ontrack.extension.git.model.ChangeLogSummary;
import net.ontrack.extension.git.model.GitImportBuildsForm;

import java.util.Locale;

public interface GitService {

    void importBuilds(int branchId, GitImportBuildsForm form);

    ChangeLogSummary getChangeLogSummary(Locale locale, int branchId, int buildFromId, int buildToId);
}
