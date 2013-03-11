package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TPromotionLevel;
import net.ontrack.core.model.Ack;

import java.util.List;

public interface PromotionLevelDao {

    List<TPromotionLevel> findByBranch(int branch);

    TPromotionLevel getById(int id);

    int createPromotionLevel(int branch, String name, String description);

    List<TPromotionLevel> findByBuild(int build);


}
