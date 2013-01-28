package net.ontrack.backend.db

interface SQL {

	// Project groups
	
	String PROJECT_GROUP_LIST = "SELECT ID, NAME, DESCRIPTION FROM PROJECT_GROUP ORDER BY NAME"
	
	String PROJECT_GROUP_CREATE = "INSERT INTO PROJECT_GROUP (NAME, DESCRIPTION) VALUES (:name, :description)"
	
	// Projects
	
	String PROJECT_LIST = "SELECT ID, NAME, DESCRIPTION FROM PROJECT ORDER BY NAME"
	
	String PROJECT_CREATE = "INSERT INTO PROJECT (NAME, DESCRIPTION) VALUES (:name, :description)"
	
	// Audit
	
	String EVENT_CREATE = """
		INSERT INTO EVENTS (AUTHOR, AUTHOR_ID, EVENT_TIMESTAMP, EVENT_TYPE, %s)
		VALUES (:author, :author_id, :event_timestamp, :event_type, :id)
		"""
	String EVENT_ALL = 'SELECT * FROM EVENTS ORDER BY ID DESC LIMIT :count OFFSET :offset'
	
	String EVENT_NAME = 'SELECT %s FROM %s WHERE ID = :id'
	
}
