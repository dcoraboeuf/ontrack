package net.ontrack.service;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.FilteredValidationStamps;

public interface ProfileService {

    FilteredValidationStamps getFilteredValidationStamps(int branchId);

    Ack removeFilterValidationStamp(int validationStampId);

    Ack addFilterValidationStamp(int validationStampId);
}
