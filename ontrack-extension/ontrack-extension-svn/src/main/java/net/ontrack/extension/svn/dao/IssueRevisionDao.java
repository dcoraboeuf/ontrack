package net.ontrack.extension.svn.dao;

import java.util.List;

public interface IssueRevisionDao {

    void link(int repository, long revision, String key);

    List<String> findIssuesByRevision(int repository, long revision);

    boolean isIndexed(int repository, String key);

    List<Long> findRevisionsByIssue(int repository, String key);
}
