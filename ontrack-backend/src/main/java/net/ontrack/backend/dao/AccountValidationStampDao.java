package net.ontrack.backend.dao;

import net.ontrack.core.model.FilteredValidationStamp;

public interface AccountValidationStampDao {

    boolean isFiltered(int account, int validationStamp);

}
