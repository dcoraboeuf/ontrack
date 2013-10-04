package net.ontrack.backend.export;

import com.google.common.base.Supplier;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import net.ontrack.core.model.Entity;

import java.util.HashMap;
import java.util.Map;

public class ImportContext {

    private final Table<Entity, Integer, Integer> table = Tables.newCustomTable(
            new HashMap<Entity, Map<Integer, Integer>>(),
            new Supplier<Map<Integer, Integer>>() {
                @Override
                public Map<Integer, Integer> get() {
                    return new HashMap<>();
                }
            }
    );

    public void forProject(int oldId, int newId) {
        table.put(Entity.PROJECT, oldId, newId);
    }

    public void forBranch(int oldBranchId, int newBranchId) {
        table.put(Entity.BRANCH, oldBranchId, newBranchId);
    }

    public int forBranch(int oldBranchId) {
        return get(Entity.BRANCH, oldBranchId);
    }

    public void forPromotionLevel(int oldPromotionLevelId, int newPromotionLevelId) {
        table.put(Entity.PROMOTION_LEVEL, oldPromotionLevelId, newPromotionLevelId);
    }

    public void forValidationStamp(int oldValidationStampId, int newValidationStampId) {
        table.put(Entity.VALIDATION_STAMP, oldValidationStampId, newValidationStampId);
    }

    public void forBuild(int oldBuildId, int newBuildId) {
        table.put(Entity.BUILD, oldBuildId, newBuildId);
    }

    public void forValidationRun(int oldValidationRunId, int newValidationRunId) {
        table.put(Entity.VALIDATION_RUN, oldValidationRunId, newValidationRunId);
    }

    public int forBuild(int oldBuildId) {
        return get(Entity.BUILD, oldBuildId);
    }

    public int forPromotionLevel(int oldPromotionLevelId) {
        return get(Entity.PROMOTION_LEVEL, oldPromotionLevelId);
    }

    public int forValidationStamp(int oldValidationStampId) {
        return get(Entity.VALIDATION_STAMP, oldValidationStampId);
    }

    private int get(Entity entity, int oldEntityId) {
        Integer newEntityId = table.get(entity, oldEntityId);
        if (newEntityId != null) {
            return newEntityId;
        } else {
            throw new ImportIdInconsistencyException(entity, oldEntityId);
        }
    }
}
