package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.jenkins.JenkinsExtension;
import net.ontrack.extension.jenkins.JenkinsUrlPropertyDescriptor;
import net.ontrack.service.ControlService;
import net.ontrack.service.ExportService;
import net.ontrack.service.ManagementService;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
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
    private PropertiesService propertiesService;
    @Autowired
    private ControlService controlService;
    @Autowired
    private ExportService exportService;
    @Autowired
    private ExtensionManager extensionManager;
    @Autowired
    private SecurityUtils securityUtils;

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
        // Makes sure the Jenkins extension is enabled
        securityUtils.asAdmin(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                extensionManager.enableExtension("jenkins");
                return null;
            }
        });
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
                // Validation stamps
                ValidationStampSummary b1smoke = managementService.createValidationStamp(b1.getId(), new ValidationStampCreationForm("SMOKE", "Smoke tests"));
                ValidationStampSummary b1acc = managementService.createValidationStamp(b1.getId(), new ValidationStampCreationForm("ACC", "Acceptance tests"));
                ValidationStampSummary b2smoke = managementService.createValidationStamp(b2.getId(), new ValidationStampCreationForm("SMOKE", "Smoke tests"));
                ValidationStampSummary b2acc = managementService.createValidationStamp(b2.getId(), new ValidationStampCreationForm("ACC", "Acceptance tests"));
                // Builds
                BuildSummary b1_build1 = controlService.createBuild(b1.getId(), new BuildCreationForm("b101", "Build 1", PropertiesCreationForm.create()));
                BuildSummary b1_build2 = controlService.createBuild(b1.getId(), new BuildCreationForm("b102", "Build 2", PropertiesCreationForm.create()));
                BuildSummary b2_build1 = controlService.createBuild(b2.getId(), new BuildCreationForm("b201", "Build 1", PropertiesCreationForm.create()));
                BuildSummary b2_build2 = controlService.createBuild(b2.getId(), new BuildCreationForm("b202", "Build 2", PropertiesCreationForm.create()));
                // Promoted runs
                PromotedRunSummary b1_build1_prdev = controlService.createPromotedRun(b1_build1.getId(), b1dev.getId(), new PromotedRunCreationForm(new DateTime(2013, 10, 3, 21, 1, DateTimeZone.UTC), "Build 1 to DEV"));
                PromotedRunSummary b1_build2_prdev = controlService.createPromotedRun(b1_build2.getId(), b1dev.getId(), new PromotedRunCreationForm(new DateTime(2013, 10, 3, 22, 1, DateTimeZone.UTC), "Build 2 to DEV"));
                PromotedRunSummary b1_build2_prprod = controlService.createPromotedRun(b1_build2.getId(), b1prod.getId(), new PromotedRunCreationForm(new DateTime(2013, 10, 3, 23, 1, DateTimeZone.UTC), "Build 2 to PROD"));
                PromotedRunSummary b2_build1_prdev = controlService.createPromotedRun(b2_build1.getId(), b2dev.getId(), new PromotedRunCreationForm(new DateTime(2013, 10, 4, 0, 1, DateTimeZone.UTC), "Build 1 to DEV"));
                // Validation runs
                ValidationRunSummary b1_build1_smoke_1 = controlService.createValidationRun(b1_build1.getId(), b1smoke.getId(), new ValidationRunCreationForm(Status.FAILED, "Failed smoke tests", PropertiesCreationForm.create()));
                ValidationRunSummary b1_build1_smoke_2 = controlService.createValidationRun(b1_build1.getId(), b1smoke.getId(), new ValidationRunCreationForm(Status.FAILED, "Failed smoke tests", PropertiesCreationForm.create()));
                ValidationRunSummary b1_build2_smoke = controlService.createValidationRun(b1_build2.getId(), b1smoke.getId(), new ValidationRunCreationForm(Status.PASSED, "Smoke tests OK", PropertiesCreationForm.create()));
                ValidationRunSummary b1_build2_acc = controlService.createValidationRun(b1_build2.getId(), b1smoke.getId(), new ValidationRunCreationForm(Status.FAILED, "Failed ACC tests", PropertiesCreationForm.create()));
                // Validation run statuses & comment
                managementService.addValidationRunComment(b1_build1_smoke_1.getId(), new ValidationRunCommentCreationForm(null, "Comment about smoke tests", Collections.<PropertyCreationForm>emptyList()));
                managementService.addValidationRunComment(b1_build1_smoke_1.getId(), new ValidationRunCommentCreationForm(Status.EXPLAINED, "Explained", Collections.<PropertyCreationForm>emptyList()));
                managementService.addValidationRunComment(b1_build1_smoke_2.getId(), new ValidationRunCommentCreationForm(Status.FIXED, "Fixed", Collections.<PropertyCreationForm>emptyList()));
                managementService.addValidationRunComment(b1_build2_acc.getId(), new ValidationRunCommentCreationForm(Status.INVESTIGATED, "Investigated", Collections.<PropertyCreationForm>emptyList()));
                // Comments
                managementService.addValidationStampComment(b1smoke.getId(), new ValidationStampCommentForm("Comment for b1smoke"));
                managementService.addValidationStampComment(b2acc.getId(), new ValidationStampCommentForm("Comment for b2acc"));
                // Properties
                propertiesService.createProperties(Entity.PROJECT, project.getId(), PropertiesCreationForm.create().with(new PropertyCreationForm(JenkinsExtension.EXTENSION, JenkinsUrlPropertyDescriptor.NAME, "uri://project")));
                propertiesService.createProperties(Entity.BRANCH, b1.getId(), PropertiesCreationForm.create().with(new PropertyCreationForm(JenkinsExtension.EXTENSION, JenkinsUrlPropertyDescriptor.NAME, "uri://branch")));
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
        ImportResult result = asAdmin().call(new Callable<ImportResult>() {
            @Override
            public ImportResult call() throws Exception {
                // Prepares the import
                ExportData importData = objectMapper.readValue(json1, ExportData.class);
                // Imports the file
                String uuid = exportService.importLaunch(importData);
                // Waits until the import is done
                ImportResult result;
                while (true) {
                    result = exportService.importCheck(uuid);
                    if (result.getFinished().isSuccess()) {
                        break;
                    } else {
                        logger.debug("Waiting for the import of the file");
                        Thread.sleep(100);
                    }
                }
                // Gets the results
                return result;
            }
        });
        assertNotNull(result);
        Collection<ProjectSummary> projects = result.getImportedProjects();
        assertEquals(1, projects.size());
        assertEquals(0, result.getRejectedProjects().size());
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
        Set<String> excludedIntFields =
                Sets.<String>union(
                        ImmutableSet.<String>of("id", "project", "branch", "build", "promotionLevel", "validationStamp", "validationRun", "authorId"),
                        ImmutableSet.<String>copyOf(
                                Collections2.transform(
                                        Arrays.asList(
                                                Entity.values()
                                        ),
                                        new Function<Entity, String>() {
                                            @Override
                                            public String apply(Entity e) {
                                                return e.name();
                                            }
                                        }
                                )
                        )
                ).immutableCopy();
        Set<String> excludedFields = ImmutableSet.of("authorId");
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
                if (!excludedFields.contains(name) && (!value.isInt() || !excludedIntFields.contains(name))) {
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
