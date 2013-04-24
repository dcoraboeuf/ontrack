package net.ontrack.core.model;

import lombok.Data;

@Data
public class PromotionLevelSummary {

	private final int id;
    private final BranchSummary branch;
    private final int levelNb;
	private final String name;
	private final String description;
    private final boolean autoPromote;

}
