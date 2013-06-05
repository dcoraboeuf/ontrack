package net.ontrack.backend.dao.model;

import lombok.Data;

import java.util.Set;

@Data
public class TBuildCleanup {

    private final int id;
    private final int branch;
    private final int retention;
    private final Set<Integer> excludedPromotionLevels;

}
