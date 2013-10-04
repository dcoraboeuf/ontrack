package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TValidationRunStatus;
import net.ontrack.core.model.Status;
import org.joda.time.DateTime;

import java.util.Collection;

public interface ValidationRunStatusDao {

    TValidationRunStatus findLastForValidationRun(int validationRunId);

    int createValidationRunStatus(int validationRun, Status status, String description, String author, Integer authorId);

    int createValidationRunStatusForImport(int validationRun, Status status, String description, String author, DateTime dateTime);

    Collection<TValidationRunStatus> findByText(String text);

    Collection<TValidationRunStatus> findByValidationRun(int validationRunId);

    void renameAuthor(int id, String name);
}
