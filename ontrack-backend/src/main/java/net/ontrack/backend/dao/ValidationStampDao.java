package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TValidationStamp;
import net.ontrack.core.model.Ack;

import java.util.List;

public interface ValidationStampDao {

    List<TValidationStamp> findByBranch(int branch);

    TValidationStamp getById(int id);

    int createValidationStamp(int branch, String name, String description);

    Ack updateValidationStamp(int id, String name, String description);

    Ack deleteValidationStamp(int id);

    List<TValidationStamp> findByPromotionLevel(int promotionLevel);

    List<TValidationStamp> findByNoPromotionLevel(int branch);

    Ack linkValidationStampToPromotionLevel(int validationStampId, int promotionLevelId);

    Ack unlinkValidationStampToPromotionLevel(int validationStampId);

    Ack updateImage(int id, byte[] image);

    byte[] getImage(int id);

    TValidationStamp getByBranchAndName(int branch, String validationStamp);

    Ack upValidationStamp(int id);

    Ack downValidationStamp(int id);

    Ack moveValidationStamp(int id, int newIndex);

    Ack setValidationStampOwner(int id, Integer ownerId);
}
