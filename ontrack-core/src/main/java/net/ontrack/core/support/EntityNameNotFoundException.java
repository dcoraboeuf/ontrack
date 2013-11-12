package net.ontrack.core.support;

import net.ontrack.core.model.Entity;

public class EntityNameNotFoundException extends NotFoundException {
	
	public EntityNameNotFoundException(Entity entity, String name) {
		super(entity, name);
	}

}
