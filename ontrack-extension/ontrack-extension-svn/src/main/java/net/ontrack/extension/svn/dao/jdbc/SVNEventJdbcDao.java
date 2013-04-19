package net.ontrack.extension.svn.dao.jdbc;


import net.ontrack.dao.AbstractJdbcDao;
import net.ontrack.extension.svn.dao.SVNEventDao;
import net.ontrack.extension.svn.dao.model.TSVNCopyEvent;
import net.ontrack.extension.svn.service.model.SVNLocation;
import net.ontrack.extension.svn.service.model.SVNLocationSortMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

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

    @Override
    @Transactional(readOnly = true)
    public TSVNCopyEvent getLastCopyEvent(String path, long revision) {
        return getFirstItem(
                "SELECT * FROM SVNCOPYEVENT WHERE COPYTOPATH = :path AND REVISION <= :revision ORDER BY REVISION DESC LIMIT 1",
                params("path", path).addValue("revision", revision),
                new RowMapper<TSVNCopyEvent>() {
                    @Override
                    public TSVNCopyEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new TSVNCopyEvent(
                                rs.getLong("revision"),
                                rs.getString("copyFromPath"),
                                rs.getLong("copyFromRevision"),
                                rs.getString("copyToPath")
                        );
                    }
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<SVNLocation> getCopiesFrom(SVNLocation location, SVNLocationSortMode sortMode) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM SVNCOPYEVENT WHERE COPYFROMPATH = :copyFromPath AND COPYFROMREVISION >= :copyFromRevision ORDER BY COPYFROMREVISION " +
                        (sortMode == SVNLocationSortMode.FROM_NEWEST ? "DESC" : "ASC"),
                params("copyFromPath", location.getPath()).addValue("copyFromRevision", location.getRevision()),
                new RowMapper<SVNLocation>() {
                    @Override
                    public SVNLocation mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new SVNLocation(
                                rs.getString("copyToPath"),
                                rs.getLong("revision")
                        );
                    }
                }
        );
    }
}
