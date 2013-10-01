package net.ontrack.backend;

import net.ontrack.core.model.ExportData;
import net.ontrack.core.model.ProjectCreationForm;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.service.ExportService;
import net.ontrack.service.ManagementService;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertNotNull;

public class ExportServiceTest extends AbstractBackendTest {

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
        ExportData exportData = asAdmin().call(new Callable<ExportData>() {
            @Override
            public ExportData call() throws Exception {
                ProjectSummary project = managementService.createProject(new ProjectCreationForm(uid("PRJ"), "Export"));
                // TODO Branches
                // TODO Promotion levels
                // TODO Validation stamps
                // TODO Builds
                // TODO Promoted runs
                // TODO Validation runs
                // TODO Validation run statuses
                // TODO Comments
                // TODO Properties
                // TODO Build clean-up policy
                // Export
                return exportProject(project);

            }
        });
        // Checks
        assertNotNull(exportData);
        // As a nice JSON...
        // TODO Gets rid of ids
        String file1 = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exportData);
    }

    private ExportData exportProject(ProjectSummary project) throws InterruptedException {
        // Exports the project
        String uuid = exportService.exportLaunch(Collections.singletonList(project.getId()));
        // Waits until the export is done
        while (!exportService.exportCheck(uuid).isSuccess()) {
            Thread.sleep(100);
        }
        // Downloads the file
        return exportService.exportDownload(uuid);
    }
}
