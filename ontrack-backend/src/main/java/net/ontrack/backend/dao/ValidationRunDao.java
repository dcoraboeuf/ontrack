package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TValidationRun;

import java.util.List;

public interface ValidationRunDao {

    TValidationRun getById(int id);

    List<TValidationRun> findByBuildAndValidationStamp(int build, int validationStamp);
}
