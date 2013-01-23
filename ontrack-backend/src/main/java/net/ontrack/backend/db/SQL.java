package net.ontrack.backend.db;

public interface SQL {
	
	String PROJECT_GROUP_LIST = "SELECT ID, NAME, DESCRIPTION FROM PROJECT_GROUP ORDER BY NAME";
	
	String PROJECT_GROUP_CREATE = "INSERT INTO PROJECT_GROUP (NAME, DESCRIPTION) VALUES (:name, :description)";

}
