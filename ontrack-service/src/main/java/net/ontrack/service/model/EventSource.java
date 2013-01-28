package net.ontrack.service.model;

public enum EventSource {
	
	PROJECT_GROUP,
	
	PROJECT;
	
	private String nameColumn;
	
	EventSource () {
		this("name");
	}
	
	EventSource (String nameColumn) {
		this.nameColumn = nameColumn;
	}
	
	public String nameColumn() {
		return nameColumn;
	}

}
