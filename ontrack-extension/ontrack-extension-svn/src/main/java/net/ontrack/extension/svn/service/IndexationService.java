package net.ontrack.extension.svn.service;

import net.ontrack.extension.svn.service.model.LastRevisionInfo;

public interface IndexationService {

    void indexFromLatest();

    void indexRange(Long from, Long to);

    void reindex();

    boolean isIndexationRunning();

    LastRevisionInfo getLastRevisionInfo(int repositoryId);
}
