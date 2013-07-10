package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.ontrack.backend.dao.FilterDao;
import net.ontrack.backend.dao.ValidationStampDao;
import net.ontrack.backend.dao.ValidationStampSelectionDao;
import net.ontrack.backend.dao.model.TValidationStamp;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.service.ManagementService;
import net.ontrack.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class DefaultProfileService implements ProfileService {

    private final SecurityUtils securityUtils;
    private final ManagementService managementService;
    private final ValidationStampDao validationStampDao;
    private final ValidationStampSelectionDao validationStampSelectionDao;
    private final FilterDao filterDao;

    @Autowired
    public DefaultProfileService(SecurityUtils securityUtils, ManagementService managementService, ValidationStampDao validationStampDao, ValidationStampSelectionDao validationStampSelectionDao, FilterDao filterDao) {
        this.securityUtils = securityUtils;
        this.managementService = managementService;
        this.validationStampDao = validationStampDao;
        this.validationStampSelectionDao = validationStampSelectionDao;
        this.filterDao = filterDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Integer> getFilteredValidationStampIds(int branchId) {
        // Gets the current user
        final int accountId = securityUtils.getCurrentAccountId();
        if (accountId > 0) {
            // Gets the complete list of validation stamps
            List<TValidationStamp> stamps = validationStampDao.findByBranch(branchId);
            // Gets the stamp ids
            List<Integer> stampIds = Lists.transform(
                    stamps,
                    new Function<TValidationStamp, Integer>() {
                        @Override
                        public Integer apply(TValidationStamp stamp) {
                            return stamp.getId();
                        }
                    }
            );
            // Filtered Ids ?
            return Sets.newHashSet(
                    Collections2.filter(
                            stampIds,
                            new Predicate<Integer>() {
                                @Override
                                public boolean apply(Integer stampId) {
                                    return validationStampSelectionDao.isFiltered(accountId, stampId);
                                }
                            }
                    )
            );
        } else {
            throw new IllegalStateException("No current account");
        }
    }

    @Override
    @Transactional
    public Ack saveFilter(int branchId, BuildFilter savedBuildFilter) {
        // Gets the current user
        final int accountId = securityUtils.getCurrentAccountId();
        if (accountId > 0) {
            return filterDao.saveFilter(accountId, branchId, savedBuildFilter);
        } else {
            throw new IllegalStateException("No current account");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuildFilter> getFilters(int branchId) {
        // Gets the current user
        final int accountId = securityUtils.getCurrentAccountId();
        if (accountId > 0) {
            return filterDao.getFilters(accountId, branchId);
        } else {
            throw new IllegalStateException("No current account");
        }
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
                                            validationStampSelectionDao.isFiltered(accountId, stamp.getId())
                                    );
                                }
                            }
                    )
            );
        } else {
            throw new IllegalStateException("No current account");
        }
    }

    @Override
    @Transactional
    public Ack removeFilterValidationStamp(int validationStampId) {
        // Gets the current user
        final int accountId = securityUtils.getCurrentAccountId();
        if (accountId > 0) {
            validationStampSelectionDao.removeFilterValidationStamp(accountId, validationStampId);
            return Ack.OK;
        } else {
            return Ack.NOK;
        }
    }

    @Override
    @Transactional
    public Ack addFilterValidationStamp(int validationStampId) {
        // Gets the current user
        final int accountId = securityUtils.getCurrentAccountId();
        if (accountId > 0) {
            validationStampSelectionDao.addFilterValidationStamp(accountId, validationStampId);
            return Ack.OK;
        } else {
            return Ack.NOK;
        }
    }

}
