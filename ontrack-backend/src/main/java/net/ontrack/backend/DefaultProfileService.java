package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.ontrack.backend.dao.AccountValidationStampDao;
import net.ontrack.core.model.FilteredValidationStamp;
import net.ontrack.core.model.FilteredValidationStamps;
import net.ontrack.core.model.ValidationStampSummary;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.service.ManagementService;
import net.ontrack.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultProfileService implements ProfileService {

    private final SecurityUtils securityUtils;
    private final ManagementService managementService;
    private final AccountValidationStampDao accountValidationStampDao;

    @Autowired
    public DefaultProfileService(SecurityUtils securityUtils, ManagementService managementService, AccountValidationStampDao accountValidationStampDao) {
        this.securityUtils = securityUtils;
        this.managementService = managementService;
        this.accountValidationStampDao = accountValidationStampDao;
    }

    @Override
    @Transactional(readOnly = true)
    public FilteredValidationStamps getFilteredValidationStamps(int branchId) {
        // Gets the current user
        final int accountId = securityUtils.getCurrentAccountId();
        if (accountId > 0) {
            return new FilteredValidationStamps(
                    managementService.getBranch(branchId),
                    Lists.transform(
                            managementService.getValidationStampList(branchId),
                            new Function<ValidationStampSummary, FilteredValidationStamp>() {
                                @Override
                                public FilteredValidationStamp apply(ValidationStampSummary stamp) {
                                    return new FilteredValidationStamp(
                                            stamp,
                                            accountValidationStampDao.isFiltered(accountId, stamp.getId())
                                    );
                                }
                            }
                    )
            );
        } else {
            throw new IllegalStateException("No current account");
        }
    }

}
