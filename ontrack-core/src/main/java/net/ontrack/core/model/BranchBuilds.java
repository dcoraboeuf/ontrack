package net.ontrack.core.model;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BranchBuilds {

    private final List<ValidationStampSummary> validationStamps;
    private final List<PromotionLevelSummary> promotionLevels;
    private final List<Status> statusList;
    private final List<BuildCompleteStatus> builds;
    private final boolean validationStampsFiltered;

    public BranchBuilds(List<ValidationStampSummary> validationStamps, List<PromotionLevelSummary> promotionLevels, List<Status> statusList, List<BuildCompleteStatus> builds) {
        this(validationStamps, promotionLevels, statusList, builds, false);
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
                    true // Filtered!
            );
        } else {
            return this;
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

    private List<ValidationStampSummary> filterValidationStamps(List<ValidationStampSummary> validationStamps, final Set<Integer> filteredStampIds) {
        return Lists.newArrayList(
                Collections2.filter(
                        validationStamps,
                        new Predicate<ValidationStampSummary>() {
                            @Override
                            public boolean apply(ValidationStampSummary validationStampSummary) {
                                return !filteredStampIds.contains(validationStampSummary.getId());
                            }
                        }
                )
        );
    }
}
