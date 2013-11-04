package net.ontrack.backend.dao;


import net.ontrack.backend.dao.model.TProjectAuthorization;
import net.ontrack.core.model.Ack;
import net.ontrack.core.security.ProjectRole;

import java.util.List;

public interface ProjectAuthorizationDao {

    List<TProjectAuthorization> findByProject(int project);

    List<TProjectAuthorization> findByAccount(int account);

    Ack set(int project, int account, ProjectRole role);

    Ack unset(int project, int account);
}
