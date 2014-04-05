package net.ontrack.extension.jira.dao;

import com.google.common.collect.Iterables;
import net.ontrack.core.model.Ack;
import net.ontrack.dao.AbstractJdbcDao;
import net.ontrack.extension.jira.service.JIRAConfigurationNameAlreadyExistsException;
import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import net.ontrack.extension.jira.service.model.JIRAConfigurationForm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Component
public class JIRAConfigurationJdbcDao extends AbstractJdbcDao implements JIRAConfigurationDao {

    private final RowMapper<JIRAConfiguration> jiraConfigurationRowMapper = new RowMapper<JIRAConfiguration>() {
        @Override
        public JIRAConfiguration mapRow(ResultSet rs, int rowNum) throws SQLException {
            String exclusions = rs.getString("exclusions");
            ExclusionsParser exclusionsParser = new ExclusionsParser(exclusions).invoke();
            Set<String> excludedProjects = exclusionsParser.getExcludedProjects();
            Set<String> excludedIssues = exclusionsParser.getExcludedIssues();
            return new JIRAConfiguration(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("url"),
                    rs.getString("user"),
                    excludedProjects,
                    excludedIssues
            );
        }
    };

    @Autowired
    public JIRAConfigurationJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<JIRAConfiguration> findAll() {
        return getJdbcTemplate().query(
                JIRASQL.JIRA_CONFIGURATION_ALL,
                jiraConfigurationRowMapper
        );
    }

    @Override
    public JIRAConfiguration create(JIRAConfigurationForm form) {
        try {
            String exclusions = getExclusionsAsString(form.getExcludedProjects(), form.getExcludedIssues());
            int id = dbCreate(
                    JIRASQL.JIRA_CONFIGURATION_CREATE,
                    params("name", form.getName())
                            .addValue("url", form.getUrl())
                            .addValue("user", form.getUser())
                            .addValue("password", form.getPassword())
                            .addValue("exclusions", exclusions)
            );
            return getById(id);
        } catch (DuplicateKeyException ex) {
            throw new JIRAConfigurationNameAlreadyExistsException(form.getName());
        }
    }

    private String getExclusionsAsString(Set<String> excludedProjects, Set<String> excludedIssues) {
        return StringUtils.join(
                Iterables.concat(
                        excludedProjects,
                        excludedIssues
                ),
                ","
        );
    }

    @Override
    public JIRAConfiguration update(int id, JIRAConfigurationForm form) {
        try {
            String password = form.getPassword();
            if (StringUtils.isBlank(password)) {
                password = getPassword(id);
            }
            String exclusions = getExclusionsAsString(form.getExcludedProjects(), form.getExcludedIssues());
            getNamedParameterJdbcTemplate().update(
                    JIRASQL.JIRA_CONFIGURATION_UPDATE,
                    params("id", id)
                            .addValue("name", form.getName())
                            .addValue("url", form.getUrl())
                            .addValue("user", form.getUser())
                            .addValue("password", password)
                            .addValue("exclusions", exclusions)
            );
            return getById(id);
        } catch (DuplicateKeyException ex) {
            throw new JIRAConfigurationNameAlreadyExistsException(form.getName());
        }
    }

    @Override
    public JIRAConfiguration getById(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                JIRASQL.JIRA_CONFIGURATION_BY_ID,
                params("id", id),
                jiraConfigurationRowMapper
        );
    }

    @Override
    public JIRAConfiguration getByName(String name) {
        return getNamedParameterJdbcTemplate().queryForObject(
                JIRASQL.JIRA_CONFIGURATION_BY_NAME,
                params("name", name),
                jiraConfigurationRowMapper
        );
    }

    @Override
    public Ack delete(int id) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        JIRASQL.JIRA_CONFIGURATION_DELETE,
                        params("id", id)
                )
        );
    }

    @Override
    public String getPassword(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                JIRASQL.JIRA_CONFIGURATION_PASSWORD,
                params("id", id),
                String.class
        );
    }
}
