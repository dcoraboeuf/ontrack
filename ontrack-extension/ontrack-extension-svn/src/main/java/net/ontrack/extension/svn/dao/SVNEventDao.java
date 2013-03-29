package net.ontrack.extension.svn.dao;

import net.ontrack.extension.svn.dao.model.TSVNCopyEvent;

public interface SVNEventDao {

    void createCopyEvent(long revision, String copyFromPath, long copyFromRevision, String copyToPath);

    void createStopEvent(long revision, String path);

    TSVNCopyEvent getLastCopyEvent(String path, long revision);
}
