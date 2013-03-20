package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TValidationRunEvent;

import java.util.List;

public interface ValidationRunEventDao {

    List<TValidationRunEvent> findByValidationRun(int validationRunId, int offset, int count);

}
