package net.ontrack.extension.svn.dao.jdbc;

import net.ontrack.dao.AbstractJdbcDao;
import net.ontrack.extension.svn.dao.IssueRevisionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

@Component
public class IssueRevisionJdbcDao extends AbstractJdbcDao implements IssueRevisionDao {

    @Autowired
    public IssueRevisionJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional
    public void link(long revision, String key) {
        getNamedParameterJdbcTemplate().update(
                "INSERT INTO REVISION_ISSUE (REVISION, ISSUE) VALUES (:revision, :key)",
                params("revision", revision).addValue("key", key));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findIssuesByRevision(long revision) {
        return getNamedParameterJdbcTemplate().queryForList(
                "SELECT ISSUE FROM REVISION_ISSUE WHERE REVISION = :revision ORDER BY ISSUE",
                params("revision", revision),
                String.class
        );
    }
}
