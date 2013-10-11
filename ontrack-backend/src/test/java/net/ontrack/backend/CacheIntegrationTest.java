package net.ontrack.backend;

import net.ontrack.core.model.ProjectSummary;
import net.ontrack.service.ManagementService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.Assert.assertEquals;

public class CacheIntegrationTest extends AbstractBackendTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private ManagementService managementService;

    @Test
    public void projectCache() throws Exception {
        // Creates a project
        ProjectSummary p = doCreateProject();
        // Gets it
        ProjectSummary p1 = managementService.getProject(p.getId());
        // Rogue update in the database
        try (Connection c = dataSource.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("UPDATE PROJECT SET NAME = ? WHERE ID = ?")) {
                ps.setString(1, uid("PRJ"));
                ps.setInt(2, p.getId());
                int count = ps.executeUpdate();
                assertEquals("Update must be done", 1, count);
            } finally {
                c.commit();
            }
        }
        // Gets it again
        ProjectSummary p2 = managementService.getProject(p.getId());
        // The project should not have been read from the database again
        assertEquals("Project name must remain the same", p1.getName(), p2.getName());
    }

}
