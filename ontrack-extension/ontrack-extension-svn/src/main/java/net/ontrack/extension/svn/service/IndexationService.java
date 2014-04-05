package net.ontrack.extension.svn.service;

import net.ontrack.extension.svn.service.model.LastRevisionInfo;

public interface IndexationService {

    void indexFromLatest(int repositoryId);

    void indexRange(int repositoryId, Long from, Long to);

    void reindex(int repositoryId);

    boolean isIndexationRunning(int repositoryId);

    LastRevisionInfo getLastRevisionInfo(int repositoryId);
}
