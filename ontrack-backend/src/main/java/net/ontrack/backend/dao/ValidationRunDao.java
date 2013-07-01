package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TValidationRun;
import net.ontrack.core.model.Ack;

import java.util.List;

public interface ValidationRunDao {

    TValidationRun getById(int id);

    List<TValidationRun> findByBuildAndValidationStamp(int build, int validationStamp);

    TValidationRun findLastByBuildAndValidationStamp(int build, int validation);

    List<TValidationRun> findLastRunsOfBuildByValidationStamp(int validationStamp, int limit);

    int createValidationRun(int build, int validationStamp, String description);

    Ack deleteById(int validationRunId);
}
