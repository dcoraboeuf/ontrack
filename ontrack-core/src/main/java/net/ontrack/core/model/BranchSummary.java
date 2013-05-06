package net.ontrack.core.model;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;

@Data
public class BranchSummary implements EntitySummary {

	private final int id;
	private final String name;
	private final String description;
	private final ProjectSummary project;

    @Override
    @JsonIgnore
    public Entity getEntity() {
        return Entity.BRANCH;
    }

}
