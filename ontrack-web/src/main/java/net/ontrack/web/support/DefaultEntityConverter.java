package net.ontrack.web.support;

import net.ontrack.core.model.Entity;
import net.ontrack.core.support.MapBuilder;
import net.ontrack.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class DefaultEntityConverter implements EntityConverter {

    private final EntityService entityService;

    @Autowired
    public DefaultEntityConverter(EntityService entityService) {
        this.entityService = entityService;
    }

    @Override
    public int getValidationRunId(String project, String branch, String build, String validationStamp, int run) {
        int buildId = getBuildId(project, branch, build);
        int validationStampId = getValidationStampId(project, branch, validationStamp);
        return getId(Entity.VALIDATION_RUN, String.valueOf(run), MapBuilder.of(Entity.BUILD, buildId).with(Entity.VALIDATION_STAMP, validationStampId).get());
    }

    @Override
    public int getValidationStampId(String project, String branch, String validationStamp) {
        int projectId = getProjectId(project);
        int branchId = getId(Entity.BRANCH, branch, Collections.singletonMap(Entity.PROJECT, projectId));
        return getId(Entity.VALIDATION_STAMP, validationStamp, MapBuilder.of(Entity.PROJECT, projectId).with(Entity.BRANCH, branchId).get());
    }

    @Override
    public int getPromotionLevelId(String project, String branch, String name) {
        int projectId = getProjectId(project);
        int branchId = getId(Entity.BRANCH, branch, Collections.singletonMap(Entity.PROJECT, projectId));
        return getId(Entity.PROMOTION_LEVEL, name, MapBuilder.of(Entity.PROJECT, projectId).with(Entity.BRANCH, branchId).get());
    }

    @Override
    public int getBuildId(String project, String branch, String validationStamp) {
        int projectId = getProjectId(project);
        int branchId = getId(Entity.BRANCH, branch, Collections.singletonMap(Entity.PROJECT, projectId));
        return getId(Entity.BUILD, validationStamp, MapBuilder.of(Entity.PROJECT, projectId).with(Entity.BRANCH, branchId).get());
    }

    @Override
    public int getBranchId(String project, String branch) {
        return getId(Entity.BRANCH, branch, Collections.singletonMap(Entity.PROJECT, getProjectId(project)));
    }

    @Override
    public int getProjectId(String project) {
        return getId(Entity.PROJECT, project, Collections.<Entity, Integer>emptyMap());
    }

    protected int getId(Entity entity, String name, Map<Entity, Integer> parentIds) {
        return entityService.getEntityId(entity, name, parentIds);
    }

}
