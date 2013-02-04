package net.ontrack.core.model;

import java.util.Arrays;
import java.util.List;

public enum Entity {
	
	PROJECT_GROUP("NAME", list(), list()),
	
	PROJECT("NAME", list(), list()),
	
	BRANCH("NAME", list(PROJECT), list(PROJECT)),
	
	BUILD("NAME", list(PROJECT, BRANCH), list(BRANCH)),
	
	VALIDATION_STAMP("NAME", list(PROJECT, BRANCH), list(BRANCH)),
	
	VALIDATION_RUN("ID", list(), list(BUILD, VALIDATION_STAMP));
	
	private String nameColumn;
	private List<Entity> context;
	private List<Entity> parents;
	
	Entity (String nameColumn, List<Entity> context, List<Entity> parents) {
		this.nameColumn = nameColumn;
		this.context = context;
		this.parents = parents;
	}
	
	public String nameColumn() {
		return nameColumn;
	}
	
	public List<Entity> getParents() {
		return parents;
	}
	
	public List<Entity> getContext() {
		return context;
	}
	
	public static List<Entity> list(Entity... entities) {
		return Arrays.asList(entities);
	}

}
