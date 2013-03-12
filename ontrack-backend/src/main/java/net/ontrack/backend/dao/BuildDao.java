package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TBuild;

import java.util.List;

public interface BuildDao {

    List<TBuild> findByBranch(int branch, int offset, int count);

    TBuild getById(int id);

    int createBuild(int branch, String name, String description);
}
