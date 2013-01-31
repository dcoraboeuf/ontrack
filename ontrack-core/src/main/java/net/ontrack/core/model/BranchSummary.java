package net.ontrack.core.model;

import lombok.Data;

@Data
public class BranchSummary {

	private final int id;
	private final String name;
	private final String description;
	private final ProjectSummary project;

}
