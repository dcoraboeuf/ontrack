package net.ontrack.backend.export;

import com.google.common.collect.Lists;
import net.ontrack.backend.dao.*;
import net.ontrack.core.model.*;
import net.ontrack.service.ManagementService;
import org.codehaus.jackson.JsonNode;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Qualifier("1.37")
public class ImportService137 implements ImportService {

    protected final ProjectDao projectDao;
    protected final BranchDao branchDao;
    protected final PromotionLevelDao promotionLevelDao;
    protected final ValidationStampDao validationStampDao;
    protected final BuildDao buildDao;
    protected final PromotedRunDao promotedRunDao;
    protected final ValidationRunDao validationRunDao;
    protected final ValidationRunStatusDao validationRunStatusDao;
    protected final EventDao eventDao;
    protected final CommentDao commentDao;
    protected final PropertyDao propertyDao;
    protected final BuildCleanupDao buildCleanupDao;
    private final ManagementService managementService;

    @Autowired
    public ImportService137(ManagementService managementService, ProjectDao projectDao, BranchDao branchDao, PromotionLevelDao promotionLevelDao, ValidationStampDao validationStampDao, BuildDao buildDao, PromotedRunDao promotedRunDao, ValidationRunDao validationRunDao, ValidationRunStatusDao validationRunStatusDao, EventDao eventDao, CommentDao commentDao, PropertyDao propertyDao, BuildCleanupDao buildCleanupDao) {
        this.managementService = managementService;
        this.projectDao = projectDao;
        this.branchDao = branchDao;
        this.promotionLevelDao = promotionLevelDao;
        this.validationStampDao = validationStampDao;
        this.buildDao = buildDao;
        this.promotedRunDao = promotedRunDao;
        this.validationRunDao = validationRunDao;
        this.validationRunStatusDao = validationRunStatusDao;
        this.eventDao = eventDao;
        this.commentDao = commentDao;
        this.propertyDao = propertyDao;
        this.buildCleanupDao = buildCleanupDao;
    }

    @Override
    @Transactional
    public ProjectSummary doImport(ProjectData projectData) {
        // Context
        ImportContext context = new ImportContext();
        // Project
        int projectId = createProject(projectData, context);
        // OK
        return managementService.getProject(projectId);
    }

    protected int createProject(ProjectData projectData, ImportContext context) {
        int projectId = importProject(projectData, context);
        importBranches(projectData, context, projectId);
        importPromotionLevels(projectData, context);
        importValidationStamps(projectData, context);
        importBuilds(projectData, context);
        importPromotedRuns(projectData, context);
        importValidationRuns(projectData, context);
        importValidationRunStatuses(projectData, context);
        importComments(projectData, context);
        importProperties(projectData, context);
        importEvents(projectData, context);
        // TODO Build clean-up policy
        // Project ID
        return projectId;
    }

    protected void importEvents(ProjectData projectData, ImportContext context) {
        List<JsonNode> eventNodeList = sortJsonNodes(projectData.getData().path("events"), "id");
        for (JsonNode eventNode : eventNodeList) {
            // int id = eventNode.path("id").asInt();
            String author = eventNode.path("author").asText();
            EventType eventType = EventType.valueOf(eventNode.path("eventType").asText());
            DateTime timestamp = new DateTime(eventNode.path("timestamp").asLong(), DateTimeZone.UTC);
            // Collects all entities
            Map<Entity, Integer> entityMap = new HashMap<>();
            Iterator<String> entities = eventNode.path("entities").getFieldNames();
            while (entities.hasNext()) {
                String entityName = entities.next();
                Entity entity = Entity.valueOf(entityName);
                int oldEntityId = eventNode.path("entities").path(entityName).asInt();
                int newEntityId = context.forEntity(entity, oldEntityId);
                entityMap.put(entity, newEntityId);
            }
            // Collects all values
            Map<String, String> valueMap = new HashMap<>();
            Iterator<String> values = eventNode.path("values").getFieldNames();
            while (values.hasNext()) {
                String key = values.next();
                String value = eventNode.path("values").path(key).asText();
                valueMap.put(key, value);
            }
            // Creation
            eventDao.importEvent(
                    author,
                    null,
                    timestamp,
                    eventType,
                    entityMap,
                    valueMap
            );
        }
    }

    protected void importProperties(ProjectData projectData, ImportContext context) {
        List<JsonNode> propertyNodeList = sortJsonNodes(projectData.getData().path("properties"), "id");
        for (JsonNode propertyNode : propertyNodeList) {
            int id = propertyNode.path("id").asInt();
            String extension = propertyNode.path("extension").asText();
            String name = propertyNode.path("name").asText();
            String value = propertyNode.path("value").asText();
            // Entity (only one is expected)
            Iterator<String> entities = propertyNode.path("entities").getFieldNames();
            if (entities.hasNext()) {
                String entityName = entities.next();
                Entity entity = Entity.valueOf(entityName);
                int oldEntityId = propertyNode.path("entities").path(entityName).asInt();
                int newEntityId = context.forEntity(entity, oldEntityId);
                propertyDao.saveProperty(
                        entity,
                        newEntityId,
                        extension,
                        name,
                        value
                );
            } else {
                throw new ImportLinkedtEntityMissingException("property", id);
            }
        }
    }

    protected void importComments(ProjectData projectData, ImportContext context) {
        List<JsonNode> commentNodeList = sortJsonNodes(projectData.getData().path("comments"), "id");
        for (JsonNode commentNode : commentNodeList) {
            int id = commentNode.path("id").asInt();
            String content = commentNode.path("content").asText();
            String author = commentNode.path("author").asText();
            DateTime timestamp = new DateTime(commentNode.path("timestamp").asLong(), DateTimeZone.UTC);
            // Entity (only one is expected)
            Iterator<String> entities = commentNode.path("entities").getFieldNames();
            if (entities.hasNext()) {
                String entityName = entities.next();
                Entity entity = Entity.valueOf(entityName);
                int oldEntityId = commentNode.path("entities").path(entityName).asInt();
                int newEntityId = context.forEntity(entity, oldEntityId);
                commentDao.importComment(
                        entity,
                        newEntityId,
                        content,
                        author,
                        null,
                        timestamp
                );
            } else {
                throw new ImportLinkedtEntityMissingException("comment", id);
            }
        }
    }

    protected void importValidationRunStatuses(ProjectData projectData, ImportContext context) {
        List<JsonNode> validationRunStatusNodeList = sortJsonNodes(projectData.getData().path("validationRunStatuses"), "id");
        for (JsonNode validationRunStatusNode : validationRunStatusNodeList) {
            int oldValidationRunId = validationRunStatusNode.path("validationRun").asInt();
            Status validationRunStatusStatus = Status.valueOf(validationRunStatusNode.path("status").asText());
            String validationRunStatusDescription = validationRunStatusNode.path("description").asText();
            String validationRunStatusAuthor = validationRunStatusNode.path("author").asText();
            DateTime validationRunStatusTimestamp = new DateTime(validationRunStatusNode.path("timestamp").asLong(), DateTimeZone.UTC);
            int newValidationRunId = context.forValidationRun(oldValidationRunId);
            validationRunStatusDao.createValidationRunStatusForImport(newValidationRunId, validationRunStatusStatus, validationRunStatusDescription, validationRunStatusAuthor, validationRunStatusTimestamp);
        }
    }

    protected void importValidationRuns(ProjectData projectData, ImportContext context) {
        List<JsonNode> validationRunNodeList = sortJsonNodes(projectData.getData().path("validationRuns"), "runOrder");
        for (JsonNode validationRunNode : validationRunNodeList) {
            int oldValidationRunId = validationRunNode.path("id").asInt();
            int oldBuildId = validationRunNode.path("build").asInt();
            int oldValidationStampId = validationRunNode.path("validationStamp").asInt();
            String validationRunDescription = validationRunNode.path("description").asText();
            int newBuildId = context.forBuild(oldBuildId);
            int newValidationStampId = context.forValidationStamp(oldValidationStampId);
            int newValidationRunId = validationRunDao.createValidationRun(newBuildId, newValidationStampId, validationRunDescription);
            context.forValidationRun(oldValidationRunId, newValidationRunId);
        }
    }

    protected void importPromotedRuns(ProjectData projectData, ImportContext context) {
        JsonNode promotedRunNodes = projectData.getData().path("promotedRuns");
        for (JsonNode promotedRunNode : promotedRunNodes) {
            int oldBuildId = promotedRunNode.path("build").asInt();
            int oldPromotionLevelId = promotedRunNode.path("promotionLevel").asInt();
            String promotedRunDescription = promotedRunNode.path("description").asText();
            String promotedRunAuthor = promotedRunNode.path("author").asText();
            DateTime promotedRunCreation = new DateTime(promotedRunNode.path("creation").asLong(), DateTimeZone.UTC);
            int newBuildId = context.forBuild(oldBuildId);
            int newPromotionLevelId = context.forPromotionLevel(oldPromotionLevelId);
            promotedRunDao.createPromotedRun(newBuildId, newPromotionLevelId, promotedRunAuthor, null, promotedRunCreation, promotedRunDescription);
        }
    }

    protected void importBuilds(ProjectData projectData, ImportContext context) {
        List<JsonNode> buildNodeList = sortJsonNodes(projectData.getData().path("builds"), "id");
        for (JsonNode buildNode : buildNodeList) {
            int oldBuildId = buildNode.path("id").asInt();
            int oldBranchId = buildNode.path("branch").asInt();
            String buildName = buildNode.path("name").asText();
            String buildDescription = buildNode.path("description").asText();
            int newBranchId = context.forBranch(oldBranchId);
            int newBuildId = buildDao.createBuild(newBranchId, buildName, buildDescription);
            context.forBuild(oldBuildId, newBuildId);
        }
    }

    protected void importValidationStamps(ProjectData projectData, ImportContext context) {
        List<JsonNode> validationStampNodeList = sortJsonNodes(projectData.getData().path("validationStamps"), "orderNb");
        for (JsonNode validationStampNode : validationStampNodeList) {
            int oldValidationStampId = validationStampNode.path("id").asInt();
            int oldBranchId = validationStampNode.path("branch").asInt();
            String validationStampName = validationStampNode.path("name").asText();
            String validationStampDescription = validationStampNode.path("description").asText();
            int newBranchId = context.forBranch(oldBranchId);
            int newValidationStampId = validationStampDao.createValidationStamp(newBranchId, validationStampName, validationStampDescription);
            context.forValidationStamp(oldValidationStampId, newValidationStampId);
        }
    }

    protected void importPromotionLevels(ProjectData projectData, ImportContext context) {
        List<JsonNode> promotionLevelsNodeList = sortJsonNodes(projectData.getData().path("promotionLevels"), "levelNb");
        for (JsonNode promotionLevelNode : promotionLevelsNodeList) {
            int oldPromotionLevelId = promotionLevelNode.path("id").asInt();
            int oldBranchId = promotionLevelNode.path("branch").asInt();
            String promotionLevelName = promotionLevelNode.path("name").asText();
            String promotionLevelDescription = promotionLevelNode.path("description").asText();
            int newBranchId = context.forBranch(oldBranchId);
            int newPromotionLevelId = promotionLevelDao.createPromotionLevel(newBranchId, promotionLevelName, promotionLevelDescription);
            context.forPromotionLevel(oldPromotionLevelId, newPromotionLevelId);
        }
    }

    protected void importBranches(ProjectData projectData, ImportContext context, int projectId) {
        JsonNode branchesNode = projectData.getData().path("branches");
        for (JsonNode branchNode : branchesNode) {
            int oldBranchId = branchNode.path("id").asInt();
            String branchName = branchNode.path("name").asText();
            String branchDescription = branchNode.path("description").asText();
            int newBranchId = branchDao.createBranch(projectId, branchName, branchDescription);
            context.forBranch(oldBranchId, newBranchId);
        }
    }

    protected int importProject(ProjectData projectData, ImportContext context) {
        int id = projectData.getData().path("project").path("id").asInt();
        String name = projectData.getData().path("project").path("name").asText();
        String description = projectData.getData().path("project").path("description").asText();
        int projectId = projectDao.createProject(name, description);
        context.forProject(id, projectId);
        return projectId;
    }

    protected List<JsonNode> sortJsonNodes(JsonNode nodes, String fieldName) {
        List<JsonNode> validationRunNodeList = Lists.newArrayList(nodes);
        Collections.sort(validationRunNodeList, getJsonFieldComparator(fieldName));
        return validationRunNodeList;
    }

    protected Comparator<JsonNode> getJsonFieldComparator(final String fieldName) {
        return new Comparator<JsonNode>() {
            @Override
            public int compare(JsonNode o1, JsonNode o2) {
                int runOrder1 = o1.path(fieldName).asInt();
                int runOrder2 = o2.path(fieldName).asInt();
                return runOrder1 - runOrder2;
            }
        };
    }

}
