package net.ontrack.extension.svn.dao;

import net.ontrack.extension.svn.dao.model.TRevision;
import org.joda.time.DateTime;

import java.util.List;

public interface RevisionDao {

    long getLast(int repositoryId);

    void addRevision(int repositoryId, long revision, String author, DateTime date, String dbMessage, String branch);

    void deleteAll(int repositoryId);

    void addMergedRevisions(int repositoryId, long revision, List<Long> mergedRevisions);

    List<Long> getMergesForRevision(long revision);

    TRevision getLastRevision(int repositoryId);

    TRevision get(long revision);
}
