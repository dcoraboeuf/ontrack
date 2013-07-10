package net.ontrack.backend.dao;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.SavedBuildFilter;

import java.util.List;

public interface FilterDao {

    Ack saveFilter(int accountId, int branchId, SavedBuildFilter savedBuildFilter);

    List<SavedBuildFilter> getFilters(int accountId, int branchId);
}
