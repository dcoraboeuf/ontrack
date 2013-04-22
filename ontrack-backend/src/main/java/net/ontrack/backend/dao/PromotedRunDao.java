package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TPromotedRun;

public interface PromotedRunDao {

    TPromotedRun findByBuildAndPromotionLevel(int build, int promotionLevel);

    int createPromotedRun(int build, int promotionLevel, String description);

    Integer findBuildByEarliestPromotion(int buildId, int promotionLevelId);
}
