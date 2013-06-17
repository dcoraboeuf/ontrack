package net.ontrack.extension.git.service;

import net.ontrack.core.security.SecurityRoles;
import net.ontrack.extension.git.model.GitImportBuildsForm;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

@Service
public class DefaultGitService implements GitService {


    @Override
    @Secured(SecurityRoles.ADMINISTRATOR)
    public void importBuilds(int branchId, GitImportBuildsForm form) {
        // FIXME Implement net.ontrack.extension.git.service.DefaultGitService.importBuilds

    }
}
