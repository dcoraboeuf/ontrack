package net.ontrack.core.model;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;

@Data
public class ProjectSummary implements EntitySummary {

	private final int id;
	private final String name;
	private final String description;

    @Override
    @JsonIgnore
    public Entity getEntity() {
        return Entity.PROJECT;
    }
}
