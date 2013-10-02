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
        Integer newBranchId = table.get(Entity.BRANCH, oldBranchId);
        if (newBranchId != null) {
            return newBranchId;
        } else {
            throw new ImportIdInconsistencyException(Entity.BRANCH, oldBranchId);
        }
    }

    public void forPromotionLevel(int oldPromotionLevelId, int newPromotionLevelId) {
        table.put(Entity.PROMOTION_LEVEL, oldPromotionLevelId, newPromotionLevelId);
    }
}
