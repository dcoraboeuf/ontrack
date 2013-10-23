package net.ontrack.backend.export;

import lombok.Data;
import net.ontrack.backend.dao.model.*;

import java.util.Collection;
import java.util.Map;

@Data
public class TExport {

    private final TProject project;
    private final Collection<TBranch> branches;
    private final Collection<TPromotionLevel> promotionLevels;
    private final Map<Integer, byte[]> promotionLevelImages;
    private final Collection<TValidationStamp> validationStamps;
    private final Collection<TBuild> builds;
    private final Collection<TPromotedRun> promotedRuns;
    private final Collection<TValidationRun> validationRuns;
    private final Collection<TValidationRunStatus> validationRunStatuses;
    private final Collection<TComment> comments;
    private final Collection<TProperty> properties;
    private final Collection<TEvent> events;
    private final Collection<TBuildCleanup> buildCleanups;

}
