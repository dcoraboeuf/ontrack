package net.ontrack.backend.dao;

import net.ontrack.core.model.Ack;

public interface DashboardDao {

    boolean isValidationStampSelectedForBranch(int validationStampId, int branchId);

    Ack associateBranchValidationStamp(int branchId, int validationStampId);

    Ack dissociateBranchValidationStamp(int branchId, int validationStampId);
}
