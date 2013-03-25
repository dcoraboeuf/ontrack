package net.ontrack.extension.svn.dao;

public interface SVNEventDao {

    void createCopyEvent(long revision, String copyFromPath, long copyFromRevision, String copyToPath);

    void createStopEvent(long revision, String path);
}
