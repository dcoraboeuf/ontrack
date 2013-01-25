package net.ontrack.backend.db

interface SQL {

	// Project groups
	
	String PROJECT_GROUP_LIST = "SELECT ID, NAME, DESCRIPTION FROM PROJECT_GROUP ORDER BY NAME"
	
	String PROJECT_GROUP_CREATE = "INSERT INTO PROJECT_GROUP (NAME, DESCRIPTION) VALUES (:name, :description)"
	
	// Projects
	
	String PROJECT_LIST = "SELECT ID, NAME, DESCRIPTION FROM PROJECT ORDER BY NAME"
	
	String PROJECT_CREATE = "INSERT INTO PROJECT (NAME, DESCRIPTION) VALUES (:name, :description)"
	
	// Audit
	
	String AUDIT_CREATE = """
		INSERT INTO AUDIT (AUTHOR, AUTHOR_ID, AUDIT_TIMESTAMP, AUDIT_CREATION, %s)
		VALUES (:author, :author_id, :audit_timestamp, :audit_creation, :id)
		"""
	String AUDIT_ALL = 'SELECT * FROM AUDIT ORDER BY ID DESC LIMIT :count OFFSET :offset'
	
}
