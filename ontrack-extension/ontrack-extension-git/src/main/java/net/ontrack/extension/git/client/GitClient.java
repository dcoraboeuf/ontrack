package net.ontrack.extension.git.client;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import net.ontrack.extension.git.model.GitConfiguration;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Collection;

public interface GitClient {

    Collection<GitTag> getTags();

    GitConfiguration getConfiguration();

    GitLog log(String from, String to);

    void sync();

    GitDiff diff(String from, String to);

    boolean isCommitDefined(String commit);

    GitCommit getCommitFor(String commit);

    String getEarliestTagForCommit(String gitCommitId, Predicate<String> tagNamePredicate);

    /**
     * Scans the whole history.
     *
     * @param scanFunction Function that scans the commits. Returns <code>true</code> if the scan
     *                     must not go on, <code>true</code> otherwise.
     * @return <code>true</code> if at least one call to <code>scanFunction</code> has returned <code>true</code>.
     */
    boolean scanCommits(Function<RevCommit, Boolean> scanFunction);
}
