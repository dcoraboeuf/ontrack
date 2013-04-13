package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TValidationRunEvent;

import java.util.List;

public interface ValidationRunEventDao {

    List<TValidationRunEvent> findByBranchAndValidationStamp(
            int validationRunId,
            int branchId,
            int validationStampId,
            int offset,
            int count);

}
