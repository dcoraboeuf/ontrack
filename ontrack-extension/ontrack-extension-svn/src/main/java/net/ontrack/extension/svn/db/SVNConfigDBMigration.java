package net.ontrack.extension.svn.db;

import net.ontrack.core.model.Entity;
import net.ontrack.dao.AbstractJdbcDao;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.svn.SubversionExtension;
import net.ontrack.extension.svn.SubversionRepositoryPropertyExtension;
import net.ontrack.extension.svn.dao.RepositoryDao;
import net.ontrack.extension.svn.service.model.SVNRepositoryForm;
import net.ontrack.service.StartupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Applies some code to convert existing SVN configuration stored at global level
 * into a list of SVN repositories, plus making sure that each project that was using
 * SVN gets now associated with the correct SVN repository.
 */
@Component
public class SVNConfigDBMigration extends AbstractJdbcDao implements StartupService {

    public static final String MIGRATION_OLD_CONFIGURATION_TEST = "SELECT COUNT(*) FROM CONFIGURATION WHERE NAME LIKE 'x-svn-%'";
    public static final String MIGRATION_OLD_CONFIGURATION_VALUES = "SELECT NAME, VALUE FROM CONFIGURATION WHERE NAME LIKE 'x-svn-%'";
    public static final String MIGRATION_OLD_CONFIGURATION_DELETE = "DELETE FROM CONFIGURATION WHERE NAME LIKE 'x-svn-%'";

    private final PropertiesService propertiesService;
    private final RepositoryDao repositoryDao;

    @Autowired
    public SVNConfigDBMigration(DataSource dataSource, PropertiesService propertiesService, RepositoryDao repositoryDao) {
        super(dataSource);
        this.propertiesService = propertiesService;
        this.repositoryDao = repositoryDao;
    }

    @Override
    public String getName() {
        return SVNConfigDBMigration.class.getSimpleName();
    }

    /**
     * Makes sure to start <i>after</i> the JIRA migration.
     */
    @Override
    public int startupOrder() {
        return 40;
    }

    /**
     * Performs the migration.
     * <p/>
     * The migration must be performed if some SVN properties remain
     * in the general <code>configuration</code> table.
     * <p/>
     * If the migration must be done, the existing properties are converted
     * into a normal repository using the name <code>default</code> and
     * added into the list of SVN repositories.
     * <p/>
     * When it is done, all projects are scanned to check if any
     * Subversion-related property is registered. If yes, the corresponding project
     * is associated with the <code>default</code> SVN repository property.
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
            // Gets the 'default' JIRA configuration if any
            // and associates it with the repository
            Integer jiraConfigurationId = getFirstItem(
                    "SELECT ID FROM EXT_JIRA_CONFIGURATION WHERE NAME = 'default'",
                    params("x", "x"),
                    Integer.class
            );
            String issueServiceId = null;
            if (jiraConfigurationId != null) {
                issueServiceId = "jira";
            }
            // Creates a new SVN repositoryand saves it with name `default`
            int repositoryId = repositoryDao.create(
                    new SVNRepositoryForm(
                            "default",
                            configuration.get("x-svn-subversion-url"),
                            configuration.get("x-svn-subversion-user"),
                            configuration.get("x-svn-subversion-password"),
                            configuration.get("x-svn-subversion-branchPattern"),
                            configuration.get("x-svn-subversion-tagPattern"),
                            configuration.get("x-svn-subversion-tagFilterPattern"),
                            configuration.get("x-svn-subversion-browserForPath"),
                            configuration.get("x-svn-subversion-browserForRevision"),
                            configuration.get("x-svn-subversion-browserForChange"),
                            Integer.parseInt(configuration.get("x-svn-indexation-scanInterval"), 10),
                            Long.parseLong(configuration.get("x-svn-indexation-startRevision"), 10),
                            issueServiceId,
                            jiraConfigurationId
                    )
            ).getId();
            // Gets all projects which have (them or one of their branches) a SVNExplorer or SVN-related property.
            List<Integer> projectIds = getJdbcTemplate().queryForList(
                    "SELECT PROJECT FROM PROPERTIES WHERE PROJECT IS NOT NULL AND (EXTENSION = 'svn' OR EXTENSION = 'svnexplorer')",
                    Integer.class
            );
            // Adds the SVN repository to the projects
            for (int projectId : projectIds) {
                propertiesService.saveProperty(
                        Entity.PROJECT,
                        projectId,
                        SubversionExtension.EXTENSION,
                        SubversionRepositoryPropertyExtension.NAME,
                        String.valueOf(repositoryId)
                );
            }
            // Migration OK, erasing previous configuration data
            getJdbcTemplate().update(MIGRATION_OLD_CONFIGURATION_DELETE);
        }
    }
}
