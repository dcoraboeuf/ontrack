package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TProjectGroup;

import java.util.List;

public interface ProjectGroupDao {

    List<TProjectGroup> findAll ();

    int createGroup(String name, String description);
}
