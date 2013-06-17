package net.ontrack.extension.git.service;

import net.ontrack.extension.git.model.GitImportBuildsForm;

public interface GitService {

    void importBuilds(int branchId, GitImportBuildsForm form);

}
