package net.ontrack.service;

import net.ontrack.core.model.FilteredValidationStamps;

public interface ProfileService {

    FilteredValidationStamps getFilteredValidationStamps(int branchId);

}
