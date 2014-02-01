package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TDashboard;
import net.ontrack.core.model.Ack;

import java.util.List;

public interface DashboardDao {

    boolean isValidationStampSelectedForBranch(int validationStampId, int branchId);

    Ack associateBranchValidationStamp(int branchId, int validationStampId);

    Ack dissociateBranchValidationStamp(int branchId, int validationStampId);

    List<TDashboard> findAllCustoms();

    int createCustom(String name, List<Integer> branches);

    void updateCustom(int id, String name, List<Integer> branches);

    TDashboard getCustom(int id);

    Ack deleteCustom(int id);
}
