package net.ontrack.backend;

import net.ontrack.core.model.Entity;
import net.ontrack.core.support.InputException;

public class EntityNameNotFoundException extends InputException {
	
	public EntityNameNotFoundException(Entity entity, String name) {
		super(entity, name);
	}

}
