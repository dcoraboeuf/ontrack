package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TProject;
import net.ontrack.core.model.Ack;

import java.util.List;

public interface ProjectDao {

    List<TProject> findAll();

    TProject getById (int id);

    int createProject(String name, String description);

    Ack deleteProject(int id);
}
