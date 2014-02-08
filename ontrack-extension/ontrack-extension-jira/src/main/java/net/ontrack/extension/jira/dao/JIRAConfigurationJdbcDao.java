package net.ontrack.extension.jira.dao;

import net.ontrack.dao.AbstractJdbcDao;
import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class JIRAConfigurationJdbcDao extends AbstractJdbcDao implements JIRAConfigurationDao {

    @Autowired
    public JIRAConfigurationJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<JIRAConfiguration> findAll() {
        return getJdbcTemplate().query(
                JIRASQL.JIRA_CONFIGURATION_ALL,
                new RowMapper<JIRAConfiguration>() {
                    @Override
                    public JIRAConfiguration mapRow(ResultSet rs, int rowNum) throws SQLException {
                        String exclusions = rs.getString("exclusions");
                        Set<String> excludedProjects = new HashSet<>();
                        Set<String> excludedIssues = new HashSet<>();
                        String[] tokens = StringUtils.split(exclusions, ",");
                        if (tokens != null) {
                            for (String token : tokens) {
                                int index = token.indexOf("-");
                                if (index > 0) {
                                    excludedIssues.add(token);
                                } else {
                                    excludedProjects.add(token);
                                }
                            }
                        }
                        return new JIRAConfiguration(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("url"),
                                rs.getString("user"),
                                rs.getString("password"),
                                excludedProjects,
                                excludedIssues
                        );
                    }
                }
        );
    }
}
