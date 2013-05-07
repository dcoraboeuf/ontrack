package net.ontrack.extension.svnexplorer.model;

import lombok.Data;
import net.ontrack.core.model.BuildPromotionLevel;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.BuildValidationStamp;
import net.ontrack.extension.svn.service.model.SVNHistory;

import java.util.List;

@Data
public class SVNBuild {
    
    private final BuildSummary buildSummary;
    private final SVNHistory history;
    private final List<BuildValidationStamp> buildValidationStamps;
    private final List<BuildPromotionLevel> buildPromotionLevels;

}
