package net.ontrack.extension.svn.dao;

import net.ontrack.extension.svn.dao.model.TSVNCopyEvent;
import net.ontrack.extension.svn.dao.model.TSVNEvent;
import net.ontrack.extension.svn.service.model.SVNLocation;
import net.ontrack.extension.svn.service.model.SVNLocationSortMode;

import java.util.Collection;
import java.util.List;

public interface SVNEventDao {

    void createCopyEvent(int repositoryId, long revision, String copyFromPath, long copyFromRevision, String copyToPath);

    void createStopEvent(int repositoryId, long revision, String path);

    TSVNCopyEvent getLastCopyEvent(int repositoryId, String path, long revision);

    Collection<SVNLocation> getCopiesFrom(int repositoryId, SVNLocation location, SVNLocationSortMode sortMode);

    Collection<SVNLocation> getCopiesFromBefore(int repositoryId, SVNLocation location, SVNLocationSortMode sortMode);

    List<TSVNEvent> getAllEvents(int repositoryId, String path);

    TSVNEvent getLastEvent(int repositoryId, String path);

    SVNLocation getFirstCopyAfter(int repositoryId, SVNLocation location);
}
