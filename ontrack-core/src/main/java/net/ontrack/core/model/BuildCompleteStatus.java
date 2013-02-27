package net.ontrack.core.model;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BuildCompleteStatus {

    // Build general info
    private final int id;
    private final String name;
    private final String description;
    // List of validation stamps with their associated runs for this build
    private final Map<String, BuildValidationStamp> validationStamps;
    // List of promotion levels for this build
    private final List<PromotionLevelSummary> promotionLevels;

    public BuildCompleteStatus(BuildSummary summary, List<BuildValidationStamp> stamps, List<PromotionLevelSummary> promotionLevels) {
        this(summary.getId(), summary.getName(), summary.getDescription(),
                new TreeMap<String, BuildValidationStamp>(
                        Maps.uniqueIndex(stamps, new Function<BuildValidationStamp, String>() {
                            @Override
                            public String apply(BuildValidationStamp stamp) {
                                return stamp.getName();
                            }
                        })
                ), promotionLevels);
    }

}
