package net.ontrack.service.model;

public enum Audited {
	
	PROJECT_GROUP,
	
	PROJECT;
	
	private String nameColumn;
	
	Audited () {
		this("name");
	}
	
	Audited (String nameColumn) {
		this.nameColumn = nameColumn;
	}
	
	public String nameColumn() {
		return nameColumn;
	}

}
