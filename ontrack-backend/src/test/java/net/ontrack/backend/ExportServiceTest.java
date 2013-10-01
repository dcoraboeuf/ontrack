package net.ontrack.backend;

import net.ontrack.core.model.*;
import net.ontrack.service.ExportService;
import net.ontrack.service.ManagementService;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertNotNull;

public class ExportServiceTest extends AbstractBackendTest {

    private final Logger logger = LoggerFactory.getLogger(ExportServiceTest.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ManagementService managementService;

    @Autowired
    private ExportService exportService;

    /**
     * This test aims to check the consistency of the export-import-export chain.
     * <p/>
     * <ol>
     * <li>A complete project structure is created</li>
     * <li>The project is export as JSON - file 1</li>
     * <li>The project is deleted</li>
     * <li>The project is re-imported from file 1</li>
     * <li>The re-imported project is exported as file 2</li>
     * <li>File 1 & 2 must be identical but for the ID attribute (which are generated at import time)</li>
     * </ol>
     */
    @Test(timeout = 2000L)
    public void create_export_delete_import_export() throws Exception {
        // Creates the project structure
        final ProjectSummary project = asAdmin().call(new Callable<ProjectSummary>() {
            @Override
            public ProjectSummary call() throws Exception {
                ProjectSummary project = managementService.createProject(new ProjectCreationForm(uid("PRJ"), "Export"));
                // Branches
                BranchSummary b1 = managementService.createBranch(project.getId(), new BranchCreationForm("B1", "B1"));
                BranchSummary b2 = managementService.createBranch(project.getId(), new BranchCreationForm("B2", "B2"));
                // Promotion levels
                PromotionLevelSummary b1dev = managementService.createPromotionLevel(b1.getId(), new PromotionLevelCreationForm("DEV", "Development"));
                PromotionLevelSummary b1prod = managementService.createPromotionLevel(b1.getId(), new PromotionLevelCreationForm("PROD", "Production"));
                PromotionLevelSummary b2dev = managementService.createPromotionLevel(b2.getId(), new PromotionLevelCreationForm("DEV", "Development"));
                PromotionLevelSummary b2prod = managementService.createPromotionLevel(b2.getId(), new PromotionLevelCreationForm("PROD", "Production"));
                // TODO Validation stamps
                // TODO Builds
                // TODO Promoted runs
                // TODO Validation runs
                // TODO Validation run statuses
                // TODO Comments
                // TODO Properties
                // TODO Build clean-up policy
                // OK
                return project;
            }
        });
        // Export
        final ExportData exportData = exportProject(project.getId());
        // Checks
        assertNotNull(exportData);
        // As a nice JSON...
        // TODO Gets rid of ids
        String file1 = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exportData);
        // Deletes the created file
        asAdmin().call(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                managementService.deleteProject(project.getId());
                return null;
            }
        });
        // TODO Imports the project
        // TODO Exports the imported project
        // TODO Compares files 1 & 2
    }

    private ExportData exportProject(final int projectId) throws Exception {
        return asAdmin().call(new Callable<ExportData>() {

            @Override
            public ExportData call() throws Exception {
                // Exports the project
                String uuid = exportService.exportLaunch(Collections.singletonList(projectId));
                // Waits until the export is done
                while (!exportService.exportCheck(uuid).isSuccess()) {
                    logger.debug("Waiting for the generation of the export file");
                    Thread.sleep(100);
                }
                // Downloads the file
                return exportService.exportDownload(uuid);
            }
        });
    }
}
