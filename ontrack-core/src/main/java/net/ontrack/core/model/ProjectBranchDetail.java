package net.ontrack.core.model;

import java.util.List;

import lombok.Data;

@Data
public class ProjectBranchDetail {
	
	private final int id;
	private final String name;
	private final String description;
	private final List<BranchOverview> branches;

}
