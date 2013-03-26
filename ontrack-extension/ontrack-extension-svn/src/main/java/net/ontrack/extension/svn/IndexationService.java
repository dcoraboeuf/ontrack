package net.ontrack.extension.svn;

public interface IndexationService {

    void indexFromLatest();

    void indexRange(Long from, Long to);

    void reindex();

    boolean isIndexationRunning();
}
