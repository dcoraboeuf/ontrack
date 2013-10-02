package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import net.ontrack.core.model.*;
import net.ontrack.service.ExportService;
import net.ontrack.service.ManagementService;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;
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
    @Test/*(timeout = 2000L)*/
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
        final ExportData export1 = exportProject(project.getId());
        // Checks
        assertNotNull(export1);
        // Gets the exported data as JSON
        final String json1 = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(export1);
        // Deletes the created file
        asAdmin().call(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                managementService.deleteProject(project.getId());
                return null;
            }
        });
        // Imports the project
        Collection<ProjectSummary> projects = asAdmin().call(new Callable<Collection<ProjectSummary>>() {
            @Override
            public Collection<ProjectSummary> call() throws Exception {
                // Prepares the import
                ExportData importData = objectMapper.readValue(json1, ExportData.class);
                // Imports the file
                String uuid = exportService.importLaunch(importData);
                // Waits until the import is done
                while (!exportService.importCheck(uuid).isSuccess()) {
                    logger.debug("Waiting for the import of the file");
                    Thread.sleep(100);
                }
                // Gets the results
                return exportService.importResults(uuid);
            }
        });
        assertNotNull(projects);
        assertEquals(1, projects.size());
        ProjectSummary importedProject = projects.iterator().next();
        assertNotNull(importedProject);
        assertEquals(project.getName(), importedProject.getName());
        // Exports the imported project
        ExportData export2 = exportProject(importedProject.getId());
        // Compares the trees without taking into account the IDs and the order of fields
        ExportData pruned1 = pruneIds(export1);
        ExportData pruned2 = pruneIds(export2);
        // Compares files 1 & 2
        String file1 = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pruned1);
        String file2 = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pruned2);
        assertEquals(file1, file2);
    }

    public ExportData pruneIds(ExportData exportData) {
        return new ExportData(
                exportData.getVersion(),
                Collections2.transform(
                        exportData.getProjects(),
                        new Function<ProjectData, ProjectData>() {
                            @Override
                            public ProjectData apply(ProjectData input) {
                                return pruneIds(input);
                            }
                        }
                )
        );
    }

    private ProjectData pruneIds(ProjectData input) {
        return new ProjectData(
                input.getName(),
                pruneIds(input.getData())
        );
    }

    private JsonNode pruneIds(JsonNode source) {
        JsonNodeFactory factory = objectMapper.getNodeFactory();
        if (source.isArray()) {
            ArrayNode target = factory.arrayNode();
            for (JsonNode sourceItem : source) {
                target.add(pruneIds(sourceItem));
            }
            return target;
        } else if (source.isObject()) {
            ObjectNode target = factory.objectNode();
            Iterator<Map.Entry<String, JsonNode>> fields = source.getFields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String name = field.getKey();
                JsonNode value = field.getValue();
                if (!"id".equals(name)) {
                    target.put(name, pruneIds(value));
                }
            }
            return target;
        } else if (source.isBoolean()) {
            return factory.booleanNode(source.asBoolean());
        } else if (source.isInt()) {
            return factory.numberNode(source.asInt());
        } else {
            return factory.textNode(source.asText());
        }
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
