package net.ontrack.extension.svn.dao;

import java.util.List;

public interface IssueRevisionDao {

    void link(long revision, String key);

    List<String> findIssuesByRevision(long revision);
}
