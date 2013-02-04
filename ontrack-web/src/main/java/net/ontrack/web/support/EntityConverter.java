package net.ontrack.web.support;

public interface EntityConverter {

	int getValidationStampId(String project, String branch, String validationStamp);

	int getBuildId(String project, String branch, String validationStamp);

	int getBranchId(String project, String branch);

	int getProjectId(String project);

}
