package net.ontrack.extension.git.client.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.ontrack.extension.git.client.*;
import net.ontrack.extension.git.client.plot.GPlot;
import net.ontrack.extension.git.client.plot.GitPlotRenderer;
import net.ontrack.extension.git.model.GitConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revplot.PlotCommitList;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.util.*;

public class DefaultGitClient implements GitClient {

    private final GitRepository repository;
    private final GitConfiguration configuration;
    private final Function<Ref, GitTag> gitTagFunction = new Function<Ref, GitTag>() {
        @Override
        public GitTag apply(Ref ref) {
            RevCommit commit = repository.getCommitForTag(ref.getObjectId());
            String tagName = StringUtils.substringAfter(
                    ref.getName(),
                    "refs/tags/"
            );
            return new GitTag(
                    tagName,
                    new DateTime(1000L * commit.getCommitTime(), DateTimeZone.UTC)
            );
        }
    };

    public DefaultGitClient(GitRepository repository, GitConfiguration configuration) {
        this.repository = repository;
        this.configuration = configuration;
    }

    @Override
    public Collection<GitTag> getTags() {
        try {
            List<GitTag> tags = new ArrayList<>(
                    Lists.transform(
                            repository.git().tagList().call(),
                            gitTagFunction)
            );
            Collections.sort(tags, new Comparator<GitTag>() {
                @Override
                public int compare(GitTag o1, GitTag o2) {
                    return o1.getTime().compareTo(o2.getTime());
                }
            });
            return tags;
        } catch (GitAPIException e) {
            throw translationException(e);
        }
    }

    @Override
    public GitConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public GitLog log(String from, String to) {
        try {
            // Client
            Git git = repository.git();
            Repository gitRepository = git.getRepository();
            // Gets boundaries
            ObjectId oFrom = gitRepository.resolve(from);
            ObjectId oTo = gitRepository.resolve(to);

            // Corresponding commits
            RevCommit commitFrom = repository.getCommitForTag(oFrom);
            RevCommit commitTo = repository.getCommitForTag(oTo);

            // Ordering of commits
            if (commitFrom.getCommitTime() < commitTo.getCommitTime()) {
                RevCommit t = commitFrom;
                commitFrom = commitTo;
                commitTo = t;
            }

            // Log
            PlotWalk walk = new PlotWalk(gitRepository);
            walk.markStart(walk.lookupCommit(commitFrom.getId()));
            walk.markUninteresting(walk.lookupCommit(commitTo.getId()));
            PlotCommitList<PlotLane> commitList = new PlotCommitList<>();
            commitList.source(walk);
            commitList.fillTo(1000); // TODO How to set the maximum? See RevWalkUtils#count ?

            // Rendering
            GitPlotRenderer renderer = new GitPlotRenderer(commitList);
            GPlot plot = renderer.getPlot();

            // Gets the commits
            List<GitCommit> commits = Lists.transform(
                    renderer.getCommits(),
                    new Function<RevCommit, GitCommit>() {
                        @Override
                        public GitCommit apply(RevCommit rev) {
                            return toCommit(rev);
                        }
                    }
            );

            // OK
            return new GitLog(
                    plot,
                    commits
            );

        } catch (IOException e) {
            throw new GitIOException(e);
        }
    }

    private String getId(RevCommit revCommit) {
        return revCommit.getId().getName();
    }

    private GitCommit toCommit(RevCommit revCommit) {
        return new GitCommit(
                getId(revCommit),
                toPerson(revCommit.getAuthorIdent()),
                toPerson(revCommit.getCommitterIdent()),
                new DateTime(1000L * revCommit.getCommitTime(), DateTimeZone.UTC),
                revCommit.getFullMessage(),
                revCommit.getShortMessage()
        );
    }

    @Override
    public void sync() {
        try {
            repository.sync();
        } catch (GitAPIException e) {
            throw translationException(e);
        }
    }

    private GitPerson toPerson(PersonIdent ident) {
        return new GitPerson(
                ident.getName(),
                ident.getEmailAddress()
        );
    }

    protected GitException translationException(GitAPIException e) {
        throw new GitException(e);
    }
}
