package net.ontrack.core.model;

import lombok.Data;

import java.util.List;

@Data
public class BuildCleanup {

    private final int retention;
    private final List<FlaggedPromotionLevel> promotionLevels;

}
