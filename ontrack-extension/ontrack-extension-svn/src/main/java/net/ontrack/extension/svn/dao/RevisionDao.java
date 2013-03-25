package net.ontrack.extension.svn.dao;

import org.joda.time.DateTime;

import java.util.List;

public interface RevisionDao {

    long getLast();

    void addRevision(long revision, String author, DateTime date, String dbMessage, String branch);

    void deleteAll();

    void addMergedRevisions(long revision, List<Long> mergedRevisions);
}
