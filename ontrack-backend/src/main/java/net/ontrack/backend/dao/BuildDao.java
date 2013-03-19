package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TBuild;
import net.ontrack.core.model.BuildFilter;

import java.util.List;

public interface BuildDao {

    List<TBuild> findByBranch(int branch, int offset, int count);

    TBuild getById(int id);

    int createBuild(int branch, String name, String description);

    List<TBuild> query(int branch, BuildFilter filter);

    TBuild findLastByBranch(int branch);
}
