package net.ontrack.backend.export;

import net.ontrack.core.model.Entity;
import net.sf.jstring.support.CoreException;

public class ImportIdInconsistencyException extends CoreException {
    public ImportIdInconsistencyException(Entity entity, int entityId) {
    }
}
