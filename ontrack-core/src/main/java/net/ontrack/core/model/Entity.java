package net.ontrack.core.model;

import java.util.Arrays;
import java.util.List;

public enum Entity {
	
	PROJECT_GROUP,
	
	PROJECT,
	
	BRANCH("name", PROJECT),
	
	VALIDATION_STAMP("name", PROJECT, BRANCH);
	
	private String nameColumn;
	private List<Entity> parents;
	
	Entity () {
		this("name");
	}
	
	Entity (String nameColumn, Entity... parents) {
		this.nameColumn = nameColumn;
		this.parents = Arrays.asList(parents);
	}
	
	public String nameColumn() {
		return nameColumn;
	}
	
	public List<Entity> getParents() {
		return parents;
	}

}
