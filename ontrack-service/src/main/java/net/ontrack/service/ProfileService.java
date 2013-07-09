package net.ontrack.service;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.FilteredValidationStamps;
import net.ontrack.core.model.SavedBuildFilter;

import java.util.Set;

public interface ProfileService {

    FilteredValidationStamps getFilteredValidationStamps(int branchId);

    Ack removeFilterValidationStamp(int validationStampId);

    Ack addFilterValidationStamp(int validationStampId);

    Set<Integer> getFilteredValidationStampIds(int branchId);

    Ack saveFilter(int branchId, SavedBuildFilter savedBuildFilter);
}
