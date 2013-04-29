package net.ontrack.core.model;

import java.util.Arrays;
import java.util.List;

public enum Entity {

	PROJECT("NAME", list(), list(), "project/{this}"),
	
	BRANCH("NAME", list(PROJECT), list(PROJECT), "project/{project}/branch/{this}"),
	
	BUILD("NAME", list(PROJECT, BRANCH), list(BRANCH), "project/{project}/branch/{branch}/build/{this}"),

    VALIDATION_STAMP("NAME", list(PROJECT, BRANCH), list(BRANCH), "project/{project}/branch/{branch}/validation_stamp/{validation_stamp}"),

    PROMOTION_LEVEL("NAME", list(PROJECT, BRANCH), list(BRANCH), "project/{project}/branch/{branch}/promotion_level/{promotion_level}"),
	
	VALIDATION_RUN("RUN_ORDER", list(PROJECT, BRANCH, BUILD, VALIDATION_STAMP), list(BUILD, VALIDATION_STAMP), "project/{project}/branch/{branch}/build/{build}/validation_stamp/{validation_stamp}/validation_run/{validation_run}");
	
	private String nameColumn;
	private List<Entity> context;
	private List<Entity> parents;
    private String uriPattern;
	
	Entity (String nameColumn, List<Entity> context, List<Entity> parents, String uriPattern) {
		this.nameColumn = nameColumn;
		this.context = context;
		this.parents = parents;
        this.uriPattern = uriPattern;
	}
	
	public String nameColumn() {
		return nameColumn;
	}
	
	public List<Entity> getParents() {
		return parents;
	}
	
	public List<Entity> getContext() {
		return context;
	}

    public String getUriPattern() {
        return uriPattern;
    }

    public static List<Entity> list(Entity... entities) {
		return Arrays.asList(entities);
	}

}
