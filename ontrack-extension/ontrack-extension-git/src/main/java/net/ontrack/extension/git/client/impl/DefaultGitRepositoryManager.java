package net.ontrack.extension.git.client.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.ontrack.service.EnvironmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.ExecutionException;

@Component
public class DefaultGitRepositoryManager implements GitRepositoryManager {

    private final Logger logger = LoggerFactory.getLogger(GitRepositoryManager.class);
    private final EnvironmentService environmentService;
    private final LoadingCache<String, GitRepository> repositoryCache =
            CacheBuilder.newBuilder()
                    .maximumSize(10)
                    .build(new CacheLoader<String, GitRepository>() {
                        @Override
                        public GitRepository load(String remote) throws Exception {
                            return createRepositoryManager(remote);
                        }
                    });

    @Autowired
    public DefaultGitRepositoryManager(EnvironmentService environmentService) {
        this.environmentService = environmentService;
    }

    private synchronized GitRepository createRepositoryManager(String remote) {
        logger.info("[git-repository] Creating repository manager for {}", remote);
        // Gets the ID for this remote location
        String id = getRepositoryId(remote);
        logger.info("[git-repository] Repository manager id for {} is {}", remote, id);
        // Gets the working directory for this ID
        File wd = environmentService.getWorkingDir("git", String.format("wd-%s", id));
        logger.debug("[git-repository] Repository manager working dir for {} is at {}", id, wd);
        // Creates the repository manager
        return new DefaultGitRepository(wd, remote, id);
    }

    @Override
    public GitRepository getRepository(String remote) {
        // Gets the cached repository managed or creates it
        try {
            return repositoryCache.get(remote);
        } catch (ExecutionException e) {
            throw new GitRepositoryManagerException(remote, e);
        }
    }

    protected String getRepositoryId(String remote) {
        return remote.replaceAll("[:\\.\\\\/@]", "_");
    }

}
