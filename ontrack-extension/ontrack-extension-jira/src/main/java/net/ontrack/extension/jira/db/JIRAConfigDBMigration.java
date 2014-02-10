package net.ontrack.extension.jira.db;

import net.ontrack.dao.AbstractJdbcDao;
import net.ontrack.extension.api.support.StartupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

/**
 * Applies some code to convert existing JIRA configurations stored at global level
 * into a list of configurations, plus making sure that each project that was using
 * JIRA gets now associated with the correct JIRA configuration.
 */
@Component
public class JIRAConfigDBMigration extends AbstractJdbcDao implements StartupService {

    @Autowired
    public JIRAConfigDBMigration(DataSource dataSource) {
        super(dataSource);
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
                "SELECT COUNT(*) FROM CONFIGURATION WHERE NAME LIKE 'x-jira.%'",
                Integer.class
        );
        if (count > 0) {
            // TODO Migration OK, erasing previous configuration data
            // getJdbcTemplate().update("DELETE FROM CONFIGURATION WHERE NAME LIKE 'x-jira.%'");
        }
    }
}
