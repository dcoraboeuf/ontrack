package net.ontrack.extension.jira.db;

import net.ontrack.core.model.Entity;
import net.ontrack.dao.AbstractJdbcDao;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.api.support.StartupService;
import net.ontrack.extension.jira.JIRAConfigurationPropertyExtension;
import net.ontrack.extension.jira.JIRAExtension;
import net.ontrack.extension.jira.dao.ExclusionsParser;
import net.ontrack.extension.jira.dao.JIRAConfigurationDao;
import net.ontrack.extension.jira.service.model.JIRAConfigurationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.*;

/**
 * Applies some code to convert existing JIRA configurations stored at global level
 * into a list of configurations, plus making sure that each project that was using
 * JIRA gets now associated with the correct JIRA configuration.
 */
@Component
public class JIRAConfigDBMigration extends AbstractJdbcDao implements StartupService {

    public static final String MIGRATION_OLD_CONFIGURATION_TEST = "SELECT COUNT(*) FROM CONFIGURATION WHERE NAME LIKE 'x-jira-configuration-%'";
    public static final String MIGRATION_OLD_CONFIGURATION_VALUES = "SELECT NAME, VALUE FROM CONFIGURATION WHERE NAME LIKE 'x-jira-configuration-%'";
    public static final String MIGRATION_OLD_CONFIGURATION_DELETE = "DELETE FROM CONFIGURATION WHERE NAME LIKE 'x-jira-configuration-%'";
    private final PropertiesService propertiesService;
    private final JIRAConfigurationDao jiraConfigurationDao;

    @Autowired
    public JIRAConfigDBMigration(DataSource dataSource, PropertiesService propertiesService, JIRAConfigurationDao jiraConfigurationDao) {
        super(dataSource);
        this.propertiesService = propertiesService;
        this.jiraConfigurationDao = jiraConfigurationDao;
    }

    @Override
    public int startupOrder() {
        return 20;
    }

    /**
     * Performs the migration.
     * <p/>
     * The migration must be performed if some JIRA properties remain
     * in the general <code>configuration</code> table.
     * <p/>
     * If the migration must be done, the existing properties are converted
     * into a normal configuration using the name <code>default</code> and
     * added into the list of JIRA configurations.
     * <p/>
     * When it is done, all projects and branches are scanned to check if any
     * Subversion-related property is registered. If yes, the corresponding project
     * is associated with the <code>default</code> JIRA configuration property.
     */
    @Override
    @Transactional
    public void start() {
        int count = getJdbcTemplate().queryForObject(
                MIGRATION_OLD_CONFIGURATION_TEST,
                Integer.class
        );
        if (count > 0) {
            // Gets the configuration attributes
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(MIGRATION_OLD_CONFIGURATION_VALUES);
            Map<String, String> configuration = new HashMap<>();
            for (Map<String, Object> row : rows) {
                configuration.put((String) row.get("name"), (String) row.get("value"));
            }
            // Exclusions
            ExclusionsParser exclusionsParser = new ExclusionsParser(configuration.get("x-jira-configuration-exclusions")).invoke();
            Set<String> excludedProjects = exclusionsParser.getExcludedProjects();
            Set<String> excludedIssues = exclusionsParser.getExcludedIssues();
            // Creates a new JIRA configuration and saves it with name `default`
            int configurationId = jiraConfigurationDao.create(
                    new JIRAConfigurationForm(
                            "default",
                            configuration.get("x-jira-configuration-url"),
                            configuration.get("x-jira-configuration-user"),
                            configuration.get("x-jira-configuration-password"),
                            excludedProjects,
                            excludedIssues
                    )
            ).getId();
            // Gets all projects which have (them or one of their branches) a SVNExplorer or SVN-related property.
            Set<Integer> projectIds = new HashSet<>();
            projectIds.addAll(
                    getJdbcTemplate().queryForList(
                            "SELECT PROJECT FROM PROPERTIES WHERE PROJECT IS NOT NULL AND (EXTENSION = 'svn' OR EXTENSION = 'svnexplorer')",
                            Integer.class
                    )
            );
            Collection<Integer> branchIds =
                    getJdbcTemplate().queryForList(
                            "SELECT BRANCH FROM PROPERTIES WHERE BRANCH IS NOT NULL AND (EXTENSION = 'svn' OR EXTENSION = 'svnexplorer')",
                            Integer.class
                    );
            for (int branchId : branchIds) {
                int projectId = getNamedParameterJdbcTemplate().queryForObject(
                        "SELECT PROJECT FROM BRANCH WHERE ID = :id",
                        params("id", branchId),
                        Integer.class
                );
                projectIds.add(projectId);
            }
            // Adds the JIRA configuration to the projects
            for (int projectId : projectIds) {
                propertiesService.saveProperty(
                        Entity.PROJECT,
                        projectId,
                        JIRAExtension.EXTENSION,
                        JIRAConfigurationPropertyExtension.NAME,
                        String.valueOf(configurationId)
                );
            }
            // Migration OK, erasing previous configuration data
            getJdbcTemplate().update(MIGRATION_OLD_CONFIGURATION_DELETE);
        }
    }
}
