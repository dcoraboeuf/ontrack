package net.ontrack.extension.svn.dao;

import net.ontrack.extension.svn.dao.model.TSVNCopyEvent;
import net.ontrack.extension.svn.service.model.SVNLocation;
import net.ontrack.extension.svn.service.model.SVNLocationSortMode;

import java.util.Collection;

public interface SVNEventDao {

    void createCopyEvent(long revision, String copyFromPath, long copyFromRevision, String copyToPath);

    void createStopEvent(long revision, String path);

    TSVNCopyEvent getLastCopyEvent(String path, long revision);

    Collection<SVNLocation> getCopiesFrom(SVNLocation location, SVNLocationSortMode sortMode);
}
