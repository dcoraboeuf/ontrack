package net.ontrack.backend;

import net.ontrack.core.model.Entity;
import net.sf.jstring.support.CoreException;

public class EntityIdNotFoundException extends CoreException {
	
	public EntityIdNotFoundException(Entity entity, int id) {
		super(entity, id);
	}

}
