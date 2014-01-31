package net.ontrack.extension.git.service;

import com.google.common.base.Function;
import net.ontrack.extension.git.model.*;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Locale;

public interface GitService {

    GitConfiguration getGitConfiguration(int branchId);

    void importBuilds(int branchId, GitImportBuildsForm form);

    ChangeLogSummary getChangeLogSummary(Locale locale, int branchId, int buildFromId, int buildToId);

    ChangeLogCommits getChangeLogCommits(Locale locale, ChangeLogSummary summary);

    ChangeLogFiles getChangeLogFiles(Locale locale, ChangeLogSummary summary);

    boolean isGitConfigured(int branchId);

    boolean isCommitDefined(String commit);

    GitCommitInfo getCommitInfo(Locale locale, String commit);

    /**
     * Scans the whole history of a branch.
     *
     * @param branchId     If of the branch to scan
     * @param scanFunction Function that scans the commits. Returns <code>true</code> if the scan
     *                     must not go on, <code>true</code> otherwise.
     * @return <code>true</code> if at least one call to <code>scanFunction</code> has returned <code>true</code>.
     */
    boolean scanCommits(int branchId, Function<RevCommit, Boolean> scanFunction);
}
