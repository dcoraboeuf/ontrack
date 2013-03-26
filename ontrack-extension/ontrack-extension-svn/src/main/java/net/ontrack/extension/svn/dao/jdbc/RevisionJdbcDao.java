package net.ontrack.extension.svn.dao.jdbc;

import net.ontrack.dao.AbstractJdbcDao;
import net.ontrack.dao.SQLUtils;
import net.ontrack.extension.svn.dao.RevisionDao;
import net.ontrack.extension.svn.dao.model.TRevision;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
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
    public long getLast() {
        return getJdbcTemplate().queryForLong("SELECT MAX(REVISION) FROM REVISION");
    }

    @Override
    @Transactional(readOnly = true)
    public TRevision getLastRevision() {
        return getFirstItem("SELECT * FROM REVISION ORDER BY REVISION DESC LIMIT 1", new MapSqlParameterSource(), revisionRowMapper);
    }

    @Override
    @Transactional
    public void addRevision(long revision, String author, DateTime date, String dbMessage, String branch) {
        NamedParameterJdbcTemplate t = getNamedParameterJdbcTemplate();
        // Getting rid of the revision
        MapSqlParameterSource params = params("revision", revision);
        t.update("DELETE FROM REVISION WHERE REVISION = :revision", params);
        // Creates the revision record
        t.update("INSERT INTO REVISION (REVISION, AUTHOR, CREATION, MESSAGE, BRANCH) VALUES (:revision, :author, :creation, :message, :branch)",
                params
                        .addValue("author", author)
                        .addValue("creation", SQLUtils.toTimestamp(date))
                        .addValue("message", StringUtils.abbreviate(dbMessage, MESSAGE_LENGTH))
                        .addValue("branch", branch)
        );
    }

    @Override
    @Transactional
    public void deleteAll() {
        getJdbcTemplate().update("DELETE FROM REVISION");
    }

    @Override
    @Transactional
    public void addMergedRevisions(long revision, List<Long> mergedRevisions) {
        NamedParameterJdbcTemplate t = getNamedParameterJdbcTemplate();
        for (long mergedRevision : mergedRevisions) {
            t.update("INSERT INTO MERGE_REVISION (REVISION, TARGET) VALUES (:mergedRevision, :revision)",
                    params("mergedRevision", mergedRevision).addValue("revision", revision));
        }
    }
}
