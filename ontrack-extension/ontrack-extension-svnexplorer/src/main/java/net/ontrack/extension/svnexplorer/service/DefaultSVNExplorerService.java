package net.ontrack.extension.svnexplorer.service;

import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.svn.SubversionExtension;
import net.ontrack.extension.svn.service.SubversionService;
import net.ontrack.extension.svn.service.model.SVNHistory;
import net.ontrack.extension.svnexplorer.model.ChangeLogRevisions;
import net.ontrack.extension.svnexplorer.model.ChangeLogSummary;
import net.ontrack.extension.svnexplorer.model.SVNBuild;
import net.ontrack.service.ManagementService;
import net.ontrack.tx.Transaction;
import net.ontrack.tx.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DefaultSVNExplorerService implements SVNExplorerService {

    private final ManagementService managementService;
    private final PropertiesService propertiesService;
    private final SubversionService subversionService;
    private final TransactionService transactionService;

    @Autowired
    public DefaultSVNExplorerService(ManagementService managementService, PropertiesService propertiesService, SubversionService subversionService, TransactionService transactionService) {
        this.managementService = managementService;
        this.propertiesService = propertiesService;
        this.subversionService = subversionService;
        this.transactionService = transactionService;
    }

    @Override
    @Transactional(readOnly = true)
    public ChangeLogSummary getChangeLogSummary(int branchId, int from, int to) {
        try (Transaction ignored = transactionService.start()) {
            // Gets the branch
            BranchSummary branch = managementService.getBranch(branchId);
            // Gets the build information
            SVNBuild buildFrom = getBuild(from);
            SVNBuild buildTo = getBuild(to);
            // OK
            return new ChangeLogSummary(
                    UUID.randomUUID().toString(),
                    branch,
                    buildFrom,
                    buildTo
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ChangeLogRevisions getChangeLogRevisions(ChangeLogSummary summary) {
        // FIXME Implement net.ontrack.extension.svnexplorer.service.DefaultSVNExplorerService.getChangeLogRevisions
        return new ChangeLogRevisions();
    }

    private SVNBuild getBuild(int buildId) {
        // Gets the build basic information
        BuildSummary build = managementService.getBuild(buildId);
        // Gets the build SVN tag
        String buildPath = getBuildPath(build.getBranch().getId(), build.getName());
        // Gets the history for this tag using the SubversionService
        SVNHistory history = subversionService.getHistory(buildPath);
        // OK
        return new SVNBuild(
                build,
                history
        );
    }

    private String getBuildPath(int branchId, String buildName) {
        // Gets the SVN build path property for the branch
        String buildPathPattern = propertiesService.getPropertyValue(Entity.BRANCH, branchId, SubversionExtension.EXTENSION, SubversionExtension.SUBVERSION_BUILD_PATH);
        if (buildPathPattern == null) {
            throw new SVNExplorerNoBuildPathDefinedForBranchException();
        }
        // Replaces the * by the build name
        return buildPathPattern.replace("*", buildName);
    }
}
