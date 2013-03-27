package net.ontrack.extension.svnexplorer.service;

import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.extension.svnexplorer.model.ChangeLogSummary;
import net.ontrack.extension.svnexplorer.model.SVNBuild;
import net.ontrack.service.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultSVNExplorerService implements SVNExplorerService {

    private final ManagementService managementService;

    @Autowired
    public DefaultSVNExplorerService(ManagementService managementService) {
        this.managementService = managementService;
    }

    @Override
    @Transactional(readOnly = true)
    public ChangeLogSummary getChangeLogSummary(int branchId, int from, int to) {
        // Gets the branch
        BranchSummary branch = managementService.getBranch(branchId);
        // Gets the build information
        SVNBuild buildFrom = getBuild(from);
        SVNBuild buildTo = getBuild(to);
        // OK
        return new ChangeLogSummary(
                branch,
                buildFrom,
                buildTo
        );
    }

    private SVNBuild getBuild(int buildId) {
        // Gets the build basic information
        BuildSummary build = managementService.getBuild(buildId);
        // OK
        return new SVNBuild(
                build,
                // FIXME History
                null
        );
    }
}
