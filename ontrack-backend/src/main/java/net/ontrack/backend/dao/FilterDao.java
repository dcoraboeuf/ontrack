package net.ontrack.backend.dao;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.SavedBuildFilter;

public interface FilterDao {

    Ack saveFilter(int accountId, int branchId, SavedBuildFilter savedBuildFilter);

}
