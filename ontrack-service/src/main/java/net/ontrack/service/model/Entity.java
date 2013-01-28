package net.ontrack.service.model;

public enum Entity {
	
	PROJECT_GROUP,
	
	PROJECT;
	
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
