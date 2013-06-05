package net.ontrack.core.model;

import lombok.Data;

import java.util.Set;

@Data
public class BuildCleanupForm {

    private final int retention;
    private final Set<Integer> excludedPromotionLevels;

}
