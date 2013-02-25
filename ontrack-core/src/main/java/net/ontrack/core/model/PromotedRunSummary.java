package net.ontrack.core.model;

import lombok.Data;

@Data
public class PromotedRunSummary {

	private final int id;
	private final String description;
	private final BuildSummary build;
	private final PromotionLevelSummary promotionLevel;

}
