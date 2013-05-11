package net.ontrack.core.model;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class PromotedRunSummary {

	private final int id;
    private final Signature signature;
    private final DateTime creation;
	private final String description;
	private final BuildSummary build;
	private final PromotionLevelSummary promotionLevel;

}
