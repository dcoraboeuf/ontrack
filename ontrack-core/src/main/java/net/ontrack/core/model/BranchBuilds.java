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

    private final List<DecoratedValidationStamp> validationStamps;
    private final List<PromotionLevelSummary> promotionLevels;
    private final List<Status> statusList;
    private final List<BuildCompleteStatus> builds;
    private final boolean validationStampsFiltered;
    private final List<BuildFilter> savedBuildFilters;

    public BranchBuilds(List<DecoratedValidationStamp> validationStamps, List<PromotionLevelSummary> promotionLevels, List<Status> statusList, List<BuildCompleteStatus> builds) {
        this(validationStamps, promotionLevels, statusList, builds, false, Collections.<BuildFilter>emptyList());
    }

    public BranchBuilds filterStamps(final Set<Integer> filteredStampIds) {
        if (filteredStampIds != null && !filteredStampIds.isEmpty()) {
            return new BranchBuilds(
                    filterValidationStamps(this.getValidationStamps(), filteredStampIds),
                    this.getPromotionLevels(),
                    this.getStatusList(),
                    Lists.transform(
                            this.getBuilds(),
                            new Function<BuildCompleteStatus, BuildCompleteStatus>() {
                                @Override
                                public BuildCompleteStatus apply(BuildCompleteStatus buildCompleteStatus) {
                                    return filterBuildCompleteStatus(buildCompleteStatus, filteredStampIds);
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

    private BuildCompleteStatus filterBuildCompleteStatus(BuildCompleteStatus buildCompleteStatus, final Set<Integer> filteredStampIds) {
        return new BuildCompleteStatus(
                buildCompleteStatus.getId(),
                buildCompleteStatus.getName(),
                buildCompleteStatus.getDescription(),
                buildCompleteStatus.getSignature(),
                buildCompleteStatus.getDecorations(),
                Lists.newArrayList(
                        Collections2.filter(
                                buildCompleteStatus.getValidationStamps().values(),
                                new Predicate<BuildValidationStamp>() {
                                    @Override
                                    public boolean apply(BuildValidationStamp buildValidationStamp) {
                                        return !filteredStampIds.contains(buildValidationStamp.getValidationStampId());
                                    }
                                }
                        )
                ),
                buildCompleteStatus.getPromotionLevels()
        );
    }

    private List<DecoratedValidationStamp> filterValidationStamps(List<DecoratedValidationStamp> validationStamps, final Set<Integer> filteredStampIds) {
        return Lists.newArrayList(
                Collections2.filter(
                        validationStamps,
                        new Predicate<DecoratedValidationStamp>() {
                            @Override
                            public boolean apply(DecoratedValidationStamp decoratedValidationStamp) {
                                return !filteredStampIds.contains(decoratedValidationStamp.getSummary().getId());
                            }
                        }
                )
        );
    }
}
