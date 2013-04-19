package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TValidationRunStatus;
import net.ontrack.core.model.Status;

import java.util.Collection;

public interface ValidationRunStatusDao {

    TValidationRunStatus findLastForValidationRun(int validationRunId);

    int createValidationRunStatus(int validationRun, Status status, String description, String author, Integer authorId);

    Collection<TValidationRunStatus> findByText(String text);

    void renameAuthor(int id, String name);
}
