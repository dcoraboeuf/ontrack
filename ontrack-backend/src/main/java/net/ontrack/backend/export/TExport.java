package net.ontrack.backend.export;

import lombok.Data;
import net.ontrack.backend.dao.model.*;

import java.util.Collection;

@Data
public class TExport {

    private final TProject project;
    private final Collection<TBranch> branches;
    private final Collection<TPromotionLevel> promotionLevels;
    private final Collection<TValidationStamp> validationStamps;
    private final Collection<TBuild> builds;
    private final Collection<TPromotedRun> promotedRuns;
    private final Collection<TValidationRun> validationRuns;
    private final Collection<TComment> comments;
    private final Collection<TEvent> events;

}
