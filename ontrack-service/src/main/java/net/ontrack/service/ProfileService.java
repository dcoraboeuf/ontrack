package net.ontrack.service;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.FilteredValidationStamps;

import java.util.Set;

public interface ProfileService {

    FilteredValidationStamps getFilteredValidationStamps(int branchId);

    Ack removeFilterValidationStamp(int validationStampId);

    Ack addFilterValidationStamp(int validationStampId);

    Set<Integer> getFilteredValidationStampIds(int branchId);
}
