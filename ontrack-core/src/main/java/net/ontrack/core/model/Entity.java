package net.ontrack.core.model;

public enum Entity {
	
	PROJECT_GROUP,
	
	PROJECT,
	
	BRANCH;
	
	private String nameColumn;
	
	Entity () {
		this("name");
	}
	
	Entity (String nameColumn) {
		this.nameColumn = nameColumn;
	}
	
	public String nameColumn() {
		return nameColumn;
	}

}
