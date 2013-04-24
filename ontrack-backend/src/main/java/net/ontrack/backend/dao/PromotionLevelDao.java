package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TPromotionLevel;
import net.ontrack.core.model.Ack;

import java.util.List;

public interface PromotionLevelDao {

    List<TPromotionLevel> findByBranch(int branch);

    TPromotionLevel getById(int id);

    int createPromotionLevel(int branch, String name, String description);

    Ack updatePromotionLevel(int promotionLevelId, String name, String description);

    List<TPromotionLevel> findByBuild(int build);

    Ack upPromotionLevel(int promotionLevelId);

    Ack downPromotionLevel(int promotionLevelId);

    Ack updateImage(int promotionLevelId, byte[] image);

    byte[] getImage(int id);

    Ack deletePromotionLevel(int promotionLevelId);

    void setAutoPromote(int promotionLevelId, boolean flag);
}
