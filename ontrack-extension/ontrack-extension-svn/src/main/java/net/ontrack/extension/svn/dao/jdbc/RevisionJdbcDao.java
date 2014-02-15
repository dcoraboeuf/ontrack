package net.ontrack.extension.svn.dao.jdbc;

import net.ontrack.dao.AbstractJdbcDao;
import net.ontrack.dao.SQLUtils;
import net.ontrack.extension.svn.RevisionNotFoundException;
import net.ontrack.extension.svn.dao.RevisionDao;
import net.ontrack.extension.svn.dao.model.TRevision;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class RevisionJdbcDao extends AbstractJdbcDao implements RevisionDao {

    public static final int MESSAGE_LENGTH = 500;
    private final RowMapper<TRevision> revisionRowMapper = new RowMapper<TRevision>() {
        @Override
        public TRevision mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TRevision(
                    rs.getInt("REPOSITORY"),
                    rs.getLong("REVISION"),
                    rs.getString("AUTHOR"),
                    SQLUtils.getDateTime(rs, "CREATION"),
                    rs.getString("MESSAGE"),
                    rs.getString("BRANCH")
            );
        }
    };

    @Autowired
    public RevisionJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public long getLast(int repositoryId) {
        Long value = getNamedParameterJdbcTemplate().queryForObject(
                "SELECT MAX(REVISION) FROM EXT_SVN_REVISION WHERE REPOSITORY = :repositoryId",
                params("repositoryId", repositoryId),
                Long.class);
        return value != null ? value : 0L;
    }

    @Override
    @Transactional(readOnly = true)
    public TRevision getLastRevision(int repositoryId) {
        return getFirstItem(
                "SELECT * FROM EXT_SVN_REVISION WHERE REPOSITORY = :repositoryId ORDER BY REVISION DESC LIMIT 1",
                params("repositoryId", repositoryId),
                revisionRowMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public TRevision get(long revision) {
        try {
            return getNamedParameterJdbcTemplate().queryForObject(
                    "SELECT * FROM REVISION WHERE REVISION = :revision",
                    params("revision", revision),
                    revisionRowMapper
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new RevisionNotFoundException(revision);
        }
    }

    @Override
    @Transactional
    public void addRevision(int repositoryId, long revision, String author, DateTime date, String dbMessage, String branch) {
        NamedParameterJdbcTemplate t = getNamedParameterJdbcTemplate();
        // Getting rid of the revision
        MapSqlParameterSource params = params("revision", revision).addValue("repositoryId", repositoryId);
        t.update("DELETE FROM EXT_SVN_REVISION WHERE REPOSITORY =:repositoryId AND REVISION = :revision", params);
        // Creates the revision record
        t.update("INSERT INTO EXT_SVN_REVISION (REPOSITORY, REVISION, AUTHOR, CREATION, MESSAGE, BRANCH) VALUES (:repositoryId, :revision, :author, :creation, :message, :branch)",
                params
                        .addValue("author", author)
                        .addValue("creation", SQLUtils.toTimestamp(date))
                        .addValue("message", StringUtils.abbreviate(dbMessage, MESSAGE_LENGTH))
                        .addValue("branch", branch)
        );
    }

    @Override
    @Transactional
    public void deleteAll(int repositoryId) {
        getNamedParameterJdbcTemplate().update(
                "DELETE FROM EXT_SVN_REVISION WHERE REPOSITORY = :repositoryId",
                params("repositoryId", repositoryId)
        );
    }

    @Override
    @Transactional
    public void addMergedRevisions(int repositoryId, long revision, List<Long> mergedRevisions) {
        NamedParameterJdbcTemplate t = getNamedParameterJdbcTemplate();
        for (long mergedRevision : mergedRevisions) {
            t.update("INSERT INTO SVN_EXT_MERGE_REVISION (REPOSITORY, REVISION, TARGET) VALUES (:repository, :mergedRevision, :revision)",
                    params("mergedRevision", mergedRevision)
                            .addValue("repository", repositoryId)
                            .addValue("revision", revision)
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getMergesForRevision(long revision) {
        return getNamedParameterJdbcTemplate().queryForList(
                "SELECT TARGET FROM MERGE_REVISION WHERE REVISION = :revision ORDER BY TARGET",
                params("revision", revision),
                Long.class
        );
    }
}
