package net.ontrack.extension.git.model;

import lombok.Data;
import net.ontrack.core.model.BuildPromotionLevel;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.BuildValidationStamp;

import java.util.List;

@Data
public class ChangeLogBuild {

    private final BuildSummary buildSummary;
    // private final SVNHistory history;
    private final List<BuildValidationStamp> buildValidationStamps;
    private final List<BuildPromotionLevel> buildPromotionLevels;

}
