package net.ontrack.extension.svn.dao;

import net.ontrack.extension.svn.dao.model.TSVNCopyEvent;
import net.ontrack.extension.svn.dao.model.TSVNEvent;
import net.ontrack.extension.svn.service.model.SVNLocation;
import net.ontrack.extension.svn.service.model.SVNLocationSortMode;

import java.util.Collection;
import java.util.List;

public interface SVNEventDao {

    void createCopyEvent(long revision, String copyFromPath, long copyFromRevision, String copyToPath);

    void createStopEvent(long revision, String path);

    TSVNCopyEvent getLastCopyEvent(String path, long revision);

    Collection<SVNLocation> getCopiesFrom(SVNLocation location, SVNLocationSortMode sortMode);

    Collection<SVNLocation> getCopiesFromBefore(SVNLocation location, SVNLocationSortMode sortMode);

    List<TSVNEvent> getAllEvents(String path);

    TSVNEvent getLastEvent(String path);

    SVNLocation getFirstCopyAfter(SVNLocation location);
}
