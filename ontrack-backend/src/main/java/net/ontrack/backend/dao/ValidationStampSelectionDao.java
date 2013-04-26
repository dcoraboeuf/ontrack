package net.ontrack.backend.dao;

public interface ValidationStampSelectionDao {

    boolean isFiltered(int account, int validationStamp);

    void removeFilterValidationStamp(int accountId, int validationStampId);

    void addFilterValidationStamp(int accountId, int validationStampId);
}
