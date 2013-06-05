package net.ontrack.core.model;

import lombok.Data;

import java.util.Set;

@Data
public class BuildCleanup {

    private final int retention;
    private final Set<PromotionLevelSummary> excludedPromotionLevels;

}
