package net.ontrack.core.model;

import lombok.Data;

@Data
public class PromotionLevel {

	private final int id;
    private final int branch;
    private final int levelNb;
	private final String name;
	private final String description;
    private final boolean autoPromote;

}
