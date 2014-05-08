package net.ontrack.core.model;

import lombok.Data;

import java.util.List;

@Data
public class BuildFilter {

    private final String name;
    private final int limit;
    private final boolean forEachPromotionLevel;
    private final String sincePromotionLevel;
    private final String withPromotionLevel;
    private final List<BuildValidationStampFilter> sinceValidationStamps;
    private final List<BuildValidationStampFilter> withValidationStamps;
    private final PropertyValue withProperty;

    public BuildFilter withName(String name) {
        return new BuildFilter(
                name,
                limit,
                forEachPromotionLevel,
                sincePromotionLevel,
                withPromotionLevel,
                sinceValidationStamps,
                withValidationStamps,
                withProperty
        );
    }
}
