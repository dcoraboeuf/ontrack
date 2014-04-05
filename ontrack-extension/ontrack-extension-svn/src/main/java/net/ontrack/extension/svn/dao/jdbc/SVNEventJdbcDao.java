package net.ontrack.extension.svn.dao.jdbc;


import net.ontrack.dao.AbstractJdbcDao;
import net.ontrack.extension.svn.SVNEventType;
import net.ontrack.extension.svn.dao.SVNEventDao;
import net.ontrack.extension.svn.dao.model.TSVNCopyEvent;
import net.ontrack.extension.svn.dao.model.TSVNEvent;
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
import java.util.List;

@Component
public class SVNEventJdbcDao extends AbstractJdbcDao implements SVNEventDao {

    private final RowMapper<SVNLocation> svnLocationRowMapper = new RowMapper<SVNLocation>() {
        @Override
        public SVNLocation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new SVNLocation(
                    rs.getString("copyToPath"),
                    rs.getLong("revision")
            );
        }
    };
    private RowMapper<TSVNEvent> eventRowMapper = new RowMapper<TSVNEvent>() {
        @Override
        public TSVNEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TSVNEvent(
                    rs.getInt(1),
                    rs.getLong(2),
                    SVNEventType.valueOf(rs.getString(3)),
                    rs.getString(4),
                    rs.getLong(5),
                    rs.getString(6)
            );
        }
    };

    @Autowired
    public SVNEventJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional
    public void createCopyEvent(int repositoryId, long revision, String copyFromPath, long copyFromRevision, String copyToPath) {
        getNamedParameterJdbcTemplate().update(
                "INSERT INTO EXT_SVN_COPY (REPOSITORY, REVISION, COPYFROMPATH, COPYFROMREVISION, COPYTOPATH) VALUES (:repository, :revision, :copyFromPath, :copyFromRevision, :copyToPath)",
                params("revision", revision)
                        .addValue("repository", repositoryId)
                        .addValue("copyFromPath", copyFromPath)
                        .addValue("copyFromRevision", copyFromRevision)
                        .addValue("copyToPath", copyToPath)
        );
    }

    @Override
    @Transactional
    public void createStopEvent(int repositoryId, long revision, String path) {
        getNamedParameterJdbcTemplate().update(
                "INSERT INTO EXT_SVN_STOP (REPOSITORY, REVISION, PATH) VALUES (:repository, :revision, :path)",
                params("revision", revision)
                        .addValue("repository", repositoryId)
                        .addValue("path", path));
    }

    @Override
    @Transactional(readOnly = true)
    public TSVNCopyEvent getLastCopyEvent(int repositoryId, String path, long revision) {
        return getFirstItem(
                "SELECT * FROM EXT_SVN_COPY WHERE REPOSITORY = :repository AND COPYTOPATH = :path AND REVISION <= :revision ORDER BY REVISION DESC LIMIT 1",
                params("path", path).addValue("revision", revision).addValue("repository", repositoryId),
                new RowMapper<TSVNCopyEvent>() {
                    @Override
                    public TSVNCopyEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new TSVNCopyEvent(
                                rs.getInt("repository"),
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
    public Collection<SVNLocation> getCopiesFromBefore(int repository, SVNLocation location, SVNLocationSortMode sortMode) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM EXT_SVN_COPY WHERE REPOSITORY = :repository AND COPYFROMPATH = :copyFromPath AND COPYFROMREVISION <= :copyFromRevision ORDER BY COPYFROMREVISION " +
                        (sortMode == SVNLocationSortMode.FROM_NEWEST ? "DESC" : "ASC"),
                params("copyFromPath", location.getPath())
                        .addValue("copyFromRevision", location.getRevision())
                        .addValue("repository", repository),
                svnLocationRowMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<SVNLocation> getCopiesFrom(int repository, SVNLocation location, SVNLocationSortMode sortMode) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM EXT_SVN_COPY WHERE REPOSITORY = :repository AND COPYFROMPATH = :copyFromPath AND COPYFROMREVISION >= :copyFromRevision ORDER BY COPYFROMREVISION " +
                        (sortMode == SVNLocationSortMode.FROM_NEWEST ? "DESC" : "ASC"),
                params("copyFromPath", location.getPath())
                        .addValue("copyFromRevision", location.getRevision())
                        .addValue("repository", repository),
                svnLocationRowMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TSVNEvent> getAllEvents(int repository, String path) {
        String sql = "(SELECT REPOSITORY, REVISION, 'STOP', PATH, 0, NULL FROM EXT_SVN_STOP WHERE REPOSITORY = :repository AND PATH = :path) "
                + "UNION "
                + "(SELECT REPOSITORY, REVISION, 'COPY', COPYFROMPATH, COPYFROMREVISION, COPYTOPATH FROM EXT_SVN_COPY WHERE REPOSITORY = :repository AND COPYFROMPATH = :path) "
                + "ORDER BY REVISION ASC ";
        return getNamedParameterJdbcTemplate().query(
                sql,
                params("path", path).addValue("repository", repository),
                eventRowMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TSVNEvent getLastEvent(int repository, String path) {
        String sql = "(SELECT REPOSITORY, REVISION, 'STOP', PATH, 0, NULL FROM EXT_SVN_STOP WHERE REPOSITORY = :repository AND PATH = :path) "
                + "UNION "
                + "(SELECT REPOSITORY, REVISION, 'COPY', COPYFROMPATH, COPYFROMREVISION, COPYTOPATH FROM EXT_SVN_COPY WHERE REPOSITORY = :repository AND COPYFROMPATH = :path) "
                + "ORDER BY REVISION DESC LIMIT 1";
        return getFirstItem(
                sql,
                params("path", path).addValue("repository", repository),
                eventRowMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SVNLocation getFirstCopyAfter(int repository, SVNLocation location) {
        return getFirstItem(
                "SELECT * FROM EXT_SVN_COPY WHERE REPOSITORY = :repository AND COPYFROMPATH = :path AND COPYFROMREVISION >= :revision",
                params("path", location.getPath()).addValue("revision", location.getRevision()).addValue("repository", repository),
                svnLocationRowMapper
        );
    }
}
