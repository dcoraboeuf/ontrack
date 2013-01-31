package net.ontrack.backend.db

interface SQL {

	// Project groups
	
	String PROJECT_GROUP_LIST = "SELECT ID, NAME, DESCRIPTION FROM PROJECT_GROUP ORDER BY NAME"
	
	String PROJECT_GROUP_CREATE = "INSERT INTO PROJECT_GROUP (NAME, DESCRIPTION) VALUES (:name, :description)"
	
	// Projects
	
	String PROJECT = "SELECT * FROM PROJECT WHERE ID = :id"
	
	String PROJECT_LIST = "SELECT ID, NAME, DESCRIPTION FROM PROJECT ORDER BY NAME"
	
	String PROJECT_CREATE = "INSERT INTO PROJECT (NAME, DESCRIPTION) VALUES (:name, :description)"
	
	String PROJECT_DELETE = "DELETE FROM PROJECT WHERE ID = :id"
	
	// Branches
	
	String BRANCH = "SELECT * FROM BRANCH WHERE ID = :id"
	
	String BRANCH_LIST = "SELECT ID, NAME, DESCRIPTION FROM BRANCH WHERE PROJECT = :project ORDER BY NAME"
	
	String BRANCH_CREATE = "INSERT INTO BRANCH (PROJECT, NAME, DESCRIPTION) VALUES (:project, :name, :description)"
	
	// Audit
	
	String EVENT_ALL = 'SELECT * FROM EVENTS ORDER BY ID DESC LIMIT :count OFFSET :offset'
	
	String EVENT_NAME = 'SELECT %s FROM %s WHERE ID = :id'

	String EVENT_VALUE_INSERT = "INSERT INTO EVENT_VALUES (EVENT, PROP_NAME, PROP_VALUE) VALUES (:id, :name, :value)";
	
	String EVENT_VALUE_LIST = "SELECT PROP_NAME, PROP_VALUE FROM EVENT_VALUES WHERE EVENT = :id";	
}
