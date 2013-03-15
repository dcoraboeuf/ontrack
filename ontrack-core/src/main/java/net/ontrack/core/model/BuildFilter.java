package net.ontrack.core.model;

import lombok.Data;

import java.util.List;

@Data
public class BuildFilter {

    private final int limit;
    private final String sincePromotionLevel;
    private final String withPromotionLevel;
    private final List<BuildValidationStampFilter> sinceValidationStamps;
    private final List<BuildValidationStampFilter> withValidationStamps;

}
