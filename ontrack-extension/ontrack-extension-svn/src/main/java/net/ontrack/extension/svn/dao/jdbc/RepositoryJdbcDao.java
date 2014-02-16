package net.ontrack.extension.svn.dao.jdbc;

import net.ontrack.core.model.Ack;
import net.ontrack.dao.AbstractJdbcDao;
import net.ontrack.extension.svn.dao.RepositoryDao;
import net.ontrack.extension.svn.dao.model.TRepository;
import net.ontrack.extension.svn.service.model.SVNRepositoryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class RepositoryJdbcDao extends AbstractJdbcDao implements RepositoryDao {

    public static final String REPOSITORY_ALL = "SELECT * FROM EXT_SVN_REPOSITORY ORDER BY NAME";
    public static final String REPOSITORY_BY_ID = "SELECT * FROM EXT_SVN_REPOSITORY WHERE ID = :id";
    public static final String REPOSITORY_DELETE = "DELETE FROM EXT_SVN_REPOSITORY WHERE ID = :id";
    public static final String REPOSITORY_INSERT = "INSERT INTO EXT_SVN_REPOSITORY(" +
            "NAME, URL, USER, PASSWORD, BRANCH_PATTERN, TAG_PATTERN, TAG_FILTER_PATTERN, BROWSER_FOR_PATH, BROWSER_FOR_REVISION, BROWSER_FOR_CHANGE, INDEXATION_INTERVAL, INDEXATION_START, ISSUE_SERVICE_NAME, ISSUE_SERVICE_CONFIG_ID" +
            ") VALUES (" +
            ":name, :url, :user, :password, :branchPattern, :tagPattern, :tagFilterPattern, :browserForPath, :browserForRevision, :browserForChange, :indexationInterval, :indexationStart, :issueServiceName, :issueServiceConfigId" +
            ")";
    public static final String REPOSITORY_UPDATE = "UPDATE EXT_SVN_REPOSITORY SET " +
            "NAME = :name, URL = :url, " +
            "USER = :user, " +
            "PASSWORD = :password, " +
            "BRANCH_PATTERN = :branchPattern, " +
            "TAG_PATTERN = :tagPattern, " +
            "TAG_FILTER_PATTERN = :tagFilterPattern, " +
            "BROWSER_FOR_PATH = :browserForPath, " +
            "BROWSER_FOR_REVISION = :browserForRevision, " +
            "BROWSER_FOR_CHANGE = :browserForChange, " +
            "INDEXATION_INTERVAL = :indexationInterval, " +
            "INDEXATION_START = :indexationStart, " +
            "ISSUE_SERVICE_NAME = :issueServiceName, " +
            "ISSUE_SERVICE_CONFIG_ID = :issueServiceConfigId " +
            "WHERE ID = :id";
    private final RowMapper<TRepository> repositoryRowMapper = new RowMapper<TRepository>() {
        @Override
        public TRepository mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TRepository(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("url"),
                    rs.getString("user"),
                    rs.getString("password"),
                    rs.getString("branch_pattern"),
                    rs.getString("tag_pattern"),
                    rs.getString("tag_filter_pattern"),
                    rs.getString("browser_for_path"),
                    rs.getString("browser_for_revision"),
                    rs.getString("browser_for_change"),
                    rs.getInt("indexation_interval"),
                    rs.getLong("indexation_start"),
                    rs.getString("issue_service_name"),
                    getInteger(rs, "issue_service_config_id")
            );
        }
    };

    @Autowired
    public RepositoryJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<TRepository> findAll() {
        return getJdbcTemplate().query(
                REPOSITORY_ALL,
                repositoryRowMapper
        );
    }

    @Override
    public TRepository create(SVNRepositoryForm form) {
        int id = dbCreate(
                REPOSITORY_INSERT,
                params("name", form.getName())
                        .addValue("url", form.getUrl())
                        .addValue("user", form.getUser())
                        .addValue("password", form.getPassword())
                        .addValue("branchPattern", form.getBranchPattern())
                        .addValue("tagPattern", form.getTagPattern())
                        .addValue("tagFilterPattern", form.getTagFilterPattern())
                        .addValue("browserForPath", form.getBrowserForPath())
                        .addValue("browserForRevision", form.getBrowserForRevision())
                        .addValue("browserForChange", form.getBrowserForChange())
                        .addValue("indexationInterval", form.getIndexationInterval())
                        .addValue("indexationStart", form.getIndexationStart())
                        .addValue("issueServiceName", form.getIssueServiceName())
                        .addValue("issueServiceConfigId", form.getIssueServiceConfigId())
        );
        return getById(id);
    }

    // TODO Caching
    @Override
    public TRepository update(int id, SVNRepositoryForm form) {
        getNamedParameterJdbcTemplate().update(
                REPOSITORY_UPDATE,
                params("id", id)
                        .addValue("name", form.getName())
                        .addValue("url", form.getUrl())
                        .addValue("user", form.getUser())
                        .addValue("password", form.getPassword())
                        .addValue("branchPattern", form.getBranchPattern())
                        .addValue("tagPattern", form.getTagPattern())
                        .addValue("tagFilterPattern", form.getTagFilterPattern())
                        .addValue("browserForPath", form.getBrowserForPath())
                        .addValue("browserForRevision", form.getBrowserForRevision())
                        .addValue("browserForChange", form.getBrowserForChange())
                        .addValue("indexationInterval", form.getIndexationInterval())
                        .addValue("indexationStart", form.getIndexationStart())
                        .addValue("issueServiceName", form.getIssueServiceName())
                        .addValue("issueServiceConfigId", form.getIssueServiceConfigId())
        );
        return getById(id);
    }

    // TODO Caching
    @Override
    public TRepository getById(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                REPOSITORY_BY_ID,
                params("id", id),
                repositoryRowMapper
        );
    }

    // TODO Caching
    @Override
    public Ack delete(int id) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        REPOSITORY_DELETE,
                        params("id", id)
                )
        );
    }
}
