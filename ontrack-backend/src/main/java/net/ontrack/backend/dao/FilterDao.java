package net.ontrack.backend.dao;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.BuildFilter;

import java.util.List;

public interface FilterDao {

    Ack saveFilter(int accountId, int branchId, BuildFilter buildFilter);

    List<BuildFilter> getFilters(int accountId, int branchId);
}
