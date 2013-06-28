package net.ontrack.core.model;

import lombok.Data;
import net.ontrack.core.model.BuildPromotionLevel;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.BuildValidationStamp;

import java.util.List;

@Data
public class BuildInfo {

    private final BuildSummary build;
    private final List<BuildPromotionLevel> buildPromotionLevels;
    private final List<BuildValidationStamp> buildValidationStamps;

}
