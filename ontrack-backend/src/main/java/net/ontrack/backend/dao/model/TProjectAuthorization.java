package net.ontrack.backend.dao.model;

import lombok.Data;
import net.ontrack.core.security.ProjectRole;

@Data
public class TProjectAuthorization {

    private final int project;
    private final int account;
    private final ProjectRole role;

}
