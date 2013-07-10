package net.ontrack.service;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.FilteredValidationStamps;
import net.ontrack.core.model.BuildFilter;

import java.util.List;
import java.util.Set;

public interface ProfileService {

    FilteredValidationStamps getFilteredValidationStamps(int branchId);

    Ack removeFilterValidationStamp(int validationStampId);

    Ack addFilterValidationStamp(int validationStampId);

    Set<Integer> getFilteredValidationStampIds(int branchId);

    Ack saveFilter(int branchId, BuildFilter savedBuildFilter);

    List<BuildFilter> getFilters(int branchId);

    Ack deleteFilter(int branchId, String name);
}
