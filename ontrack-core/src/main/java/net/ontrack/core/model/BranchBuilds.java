package net.ontrack.core.model;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BranchBuilds {

    private final List<ValidationStamp> validationStamps;
    private final List<PromotionLevel> promotionLevels;
    private final List<Status> statusList;
    private final List<BranchBuild> builds;
    private final boolean validationStampsFiltered;
    private final List<BuildFilter> savedBuildFilters;

    public BranchBuilds(List<ValidationStamp> validationStamps, List<PromotionLevel> promotionLevels, List<Status> statusList, List<BranchBuild> builds) {
        this(
                validationStamps,
                promotionLevels,
                statusList,
                builds,
                false,
                Collections.<BuildFilter>emptyList());
    }

    public BranchBuilds filterStamps(final Set<Integer> filteredStampIds) {
        if (filteredStampIds != null && !filteredStampIds.isEmpty()) {
            return new BranchBuilds(
                    filterValidationStamps(this.getValidationStamps(), filteredStampIds),
                    this.getPromotionLevels(),
                    this.getStatusList(),
                    Lists.transform(
                            this.getBuilds(),
                            new Function<BranchBuild, BranchBuild>() {
                                @Override
                                public BranchBuild apply(BranchBuild branchBuild) {
                                    return filterBranchBuildValidationStamps(branchBuild, filteredStampIds);
                                }
                            }
                    ),
                    true, // Filtered!
                    savedBuildFilters
            );
        } else {
            return this;
        }
    }

    public BranchBuilds withSavedBuildFilters(List<BuildFilter> filters) {
        if (filters == null || filters.isEmpty()) {
            return this;
        } else {
            return new BranchBuilds(
                    validationStamps,
                    promotionLevels,
                    statusList,
                    builds,
                    validationStampsFiltered,
                    filters
            );
        }
    }

    private BranchBuild filterBranchBuildValidationStamps(BranchBuild branchBuild, final Set<Integer> filteredStampIds) {
        return new BranchBuild(
                branchBuild.getId(),
                branchBuild.getName(),
                branchBuild.getDescription(),
                branchBuild.getSignature(),
                branchBuild.getDecorations(),
                Lists.newArrayList(
                        Collections2.filter(
                                branchBuild.getValidationStamps().values(),
                                new Predicate<BranchBuildValidationStampLastStatus>() {
                                    @Override
                                    public boolean apply(BranchBuildValidationStampLastStatus buildValidationStamp) {
                                        return !filteredStampIds.contains(buildValidationStamp.getValidationStampId());
                                    }
                                }
                        )
                ),
                branchBuild.getPromotionLevels()
        );
    }

    private List<ValidationStamp> filterValidationStamps(List<ValidationStamp> validationStamps, final Set<Integer> filteredStampIds) {
        return Lists.newArrayList(
                Collections2.filter(
                        validationStamps,
                        new Predicate<ValidationStamp>() {
                            @Override
                            public boolean apply(ValidationStamp validationStamp) {
                                return !filteredStampIds.contains(validationStamp.getId());
                            }
                        }
                )
        );
    }
}
