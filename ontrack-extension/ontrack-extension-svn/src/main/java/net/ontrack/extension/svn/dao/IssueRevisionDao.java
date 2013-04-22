package net.ontrack.extension.svn.dao;

import java.util.List;

public interface IssueRevisionDao {

    void link(long revision, String key);

    List<String> findIssuesByRevision(long revision);

    boolean isIndexed(String key);

    List<Long> findRevisionsByIssue(String key);
}
