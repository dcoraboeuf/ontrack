package net.ontrack.extension.svn.dao.jdbc;

import net.ontrack.dao.AbstractJdbcDao;
import net.ontrack.extension.svn.dao.IssueRevisionDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

@Component
public class IssueRevisionJdbcDao extends AbstractJdbcDao implements IssueRevisionDao {

    private static final int ISSUE_KEY_MAX_LENGTH = 20;
    private final Logger logger = LoggerFactory.getLogger(IssueRevisionDao.class);

    @Autowired
    public IssueRevisionJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional
    public void link(int repository, long revision, String key) {
        if (StringUtils.isBlank(key)) {
            logger.warn("Cannot insert a null or blank key (revision {})", revision);
        } else if (key.length() > ISSUE_KEY_MAX_LENGTH) {
            logger.warn("Cannot insert a key longer than {} characters: {} for revision {}", ISSUE_KEY_MAX_LENGTH, key, revision);
        } else {
            getNamedParameterJdbcTemplate().update(
                    "INSERT INTO EXT_SVN_REVISION_ISSUE (REPOSITORY, REVISION, ISSUE) VALUES (:repository, :revision, :key)",
                    params("revision", revision).addValue("key", key).addValue("repository", repository));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findIssuesByRevision(int repository, long revision) {
        return getNamedParameterJdbcTemplate().queryForList(
                "SELECT ISSUE FROM EXT_SVN_REVISION_ISSUE WHERE REPOSITORY = :repository AND REVISION = :revision ORDER BY ISSUE",
                params("revision", revision).addValue("repository", repository),
                String.class
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isIndexed(int repository, String key) {
        return getFirstItem(
                "SELECT ISSUE FROM EXT_SVN_REVISION_ISSUE WHERE REPOSITORY = :repository AND ISSUE = :key",
                params("key", key).addValue("repository", repository),
                String.class) != null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findRevisionsByIssue(int repository, String key) {
        return getNamedParameterJdbcTemplate().queryForList(
                "SELECT REVISION FROM EXT_SVN_REVISION_ISSUE WHERE REPOSITORY = :repository AND ISSUE = :key ORDER BY REVISION DESC",
                params("key", key).addValue("repository", repository),
                Long.class);
    }

}
