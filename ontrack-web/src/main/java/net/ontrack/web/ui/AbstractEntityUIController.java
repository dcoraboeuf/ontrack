package net.ontrack.web.ui;

import java.util.Collections;
import java.util.Map;

import net.ontrack.core.model.Entity;
import net.ontrack.core.support.MapBuilder;
import net.ontrack.service.ManagementService;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;

public class AbstractEntityUIController extends AbstractUIController {

	protected final ManagementService managementService;

	public AbstractEntityUIController(ErrorHandler errorHandler, Strings strings, ManagementService managementService) {
		super(errorHandler, strings);
		this.managementService = managementService;
	}
	
	// Common

	protected int getValidationStampId(String project, String branch, String validationStamp) {
		int projectId = getProjectId(project);
		int branchId = getId(Entity.BRANCH, branch, Collections.singletonMap(Entity.PROJECT, projectId));
		int validationStampId = getId(Entity.VALIDATION_STAMP, validationStamp, MapBuilder.create(Entity.PROJECT, projectId).with(Entity.BRANCH, branchId).build());
		return validationStampId;
	}

	protected int getBranchId(String project, String branch) {
		int projectId = getProjectId(project);
		int branchId = getId(Entity.BRANCH, branch, Collections.singletonMap(Entity.PROJECT, projectId));
		return branchId;
	}

	protected int getProjectId(String project) {
		int projectId = getId(Entity.PROJECT, project, Collections.<Entity, Integer>emptyMap());
		return projectId;
	}

	protected int getId(Entity entity, String name, Map<Entity, Integer> parentIds) {
		return managementService.getEntityId(entity, name, parentIds);
	}

}
