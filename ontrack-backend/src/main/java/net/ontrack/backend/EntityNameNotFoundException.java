package net.ontrack.backend;

import net.ontrack.core.model.Entity;
import net.ontrack.core.support.NotFoundException;

public class EntityNameNotFoundException extends NotFoundException {
	
	public EntityNameNotFoundException(Entity entity, String name) {
		super(entity, name);
	}

}
