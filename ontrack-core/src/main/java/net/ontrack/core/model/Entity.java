package net.ontrack.core.model;

import java.util.Arrays;
import java.util.List;

public enum Entity {
	
	PROJECT_GROUP,
	
	PROJECT,
	
	BRANCH("name", "project");
	
	private String nameColumn;
	private List<String> parentColumns;
	
	Entity () {
		this("name");
	}
	
	Entity (String nameColumn, String... parentColumns) {
		this.nameColumn = nameColumn;
		this.parentColumns = Arrays.asList(parentColumns);
	}
	
	public String nameColumn() {
		return nameColumn;
	}
	
	public List<String> getParentColumns() {
		return parentColumns;
	}

}
