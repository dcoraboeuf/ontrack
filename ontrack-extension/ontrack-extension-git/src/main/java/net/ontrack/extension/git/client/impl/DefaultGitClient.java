package net.ontrack.extension.git.client.impl;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import net.ontrack.extension.git.client.GitClient;
import net.ontrack.extension.git.client.GitTag;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

import java.util.Collection;

public class DefaultGitClient implements GitClient {

    private final GitRepository repository;
    private final Function<Ref, GitTag> gitTagFunction = new Function<Ref, GitTag>() {
        @Override
        public GitTag apply(Ref ref) {
            return new GitTag(
                    ref.getName()
            );
        }
    };

    public DefaultGitClient(GitRepository repository) {
        this.repository = repository;
    }

    @Override
    public Collection<GitTag> getTags() {
        try {
            return Collections2.transform(
                    repository.sync().git().tagList().call(),
                    gitTagFunction);
        } catch (GitAPIException e) {
            throw translationException(e);
        }
    }

    protected GitException translationException(GitAPIException e) {
        throw new GitException(e);
    }
}
