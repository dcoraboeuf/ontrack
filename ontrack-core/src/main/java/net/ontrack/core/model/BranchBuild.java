package net.ontrack.core.model;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Defines a build in the list of builds for a branch.
 *
 * @see BranchBuilds
 */
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BranchBuild {

    // Build general info
    private final int id;
    private final String name;
    private final String description;
    private final DatedSignature signature;
    // List of decorations
    private final List<LocalizedDecoration> decorations;
    /**
     * Index of last build status for each validation stamp. Map from ValidationStamp id to
     */
    private final Map<Integer, BranchBuildValidationStampLastStatus> validationStamps;
    // List of promotion levels for this build
    private final List<BuildPromotionLevel> promotionLevels;

    public BranchBuild(int id, String name, String description, DatedSignature signature, List<LocalizedDecoration> decorations, List<BranchBuildValidationStampLastStatus> stamps, List<BuildPromotionLevel> promotionLevels) {
        this(
                id, name, description,
                signature,
                decorations,
                new TreeMap<>(
                        Maps.uniqueIndex(stamps, new Function<BranchBuildValidationStampLastStatus, Integer>() {
                            @Override
                            public Integer apply(BranchBuildValidationStampLastStatus stamp) {
                                return stamp.getValidationStampId();
                            }
                        })
                ), promotionLevels);
    }

}
