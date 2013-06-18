package net.ontrack.extension.git.client.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.ontrack.extension.git.client.GitClient;
import net.ontrack.extension.git.client.GitTag;
import net.ontrack.extension.git.model.GitConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
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
            RevCommit commit = repository.getCommitForTag(ref);
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
                            repository.sync().git().tagList().call(),
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
    public void log(String from, String to) {
        try {
            // Client
            Git git = repository.sync().git();
            // Gets boundaries
            ObjectId oFrom = git.getRepository().resolve(from);
            ObjectId oTo = git.getRepository().resolve(to);
            // Log
            Iterable<RevCommit> commits = git.log().addRange(oFrom, oTo).call();
            System.out.println(commits);
        } catch (GitAPIException e) {
            throw translationException(e);
        } catch (IOException e) {
            throw new GitIOException(e);
        }

    }

    protected GitException translationException(GitAPIException e) {
        throw new GitException(e);
    }
}
