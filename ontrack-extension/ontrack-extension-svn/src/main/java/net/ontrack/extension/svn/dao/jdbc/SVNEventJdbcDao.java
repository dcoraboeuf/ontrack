package net.ontrack.extension.svn.dao.jdbc;


import net.ontrack.dao.AbstractJdbcDao;
import net.ontrack.extension.svn.dao.SVNEventDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Component
public class SVNEventJdbcDao extends AbstractJdbcDao implements SVNEventDao {

    @Autowired
    public SVNEventJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional
    public void createCopyEvent(long revision, String copyFromPath, long copyFromRevision, String copyToPath) {
        getNamedParameterJdbcTemplate().update(
                "INSERT INTO SVNCOPYEVENT (REVISION, COPYFROMPATH, COPYFROMREVISION, COPYTOPATH) VALUES (:revision, :copyFromPath, :copyFromRevision, :copyToPath)",
                params("revision", revision)
                        .addValue("copyFromPath", copyFromPath)
                        .addValue("copyFromRevision", copyFromRevision)
                        .addValue("copyToPath", copyToPath)
        );
    }

    @Override
    @Transactional
    public void createStopEvent(long revision, String path) {
        getNamedParameterJdbcTemplate().update(
                "INSERT INTO SVNSTOPEVENT (REVISION, PATH) VALUES (:revision, :path)",
                params("revision", revision)
                        .addValue("path", path));
    }
}
