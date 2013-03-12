package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TValidationRunStatus;
import net.ontrack.core.model.Status;

public interface ValidationRunStatusDao {

    TValidationRunStatus findLastForValidationRun(int validationRunId);

    int createValidationRunStatus(int validationRun, Status status, String description, String author, Integer authorId);
}
