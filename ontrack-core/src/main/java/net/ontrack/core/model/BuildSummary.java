package net.ontrack.core.model;

import lombok.Data;

@Data
public class BuildSummary {

	private final int id;
	private final String name;
	private final String description;
	private final BranchSummary branch;

}
