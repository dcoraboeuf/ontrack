package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TBranch;
import net.ontrack.core.model.Ack;

import java.util.Collection;
import java.util.List;

public interface BranchDao {

    List<TBranch> findByProject(int project);

    TBranch getById(int id);

    int createBranch(int project, String name, String description);

    Ack deleteBranch(int id);

    Collection<TBranch> findByName(String name);
}
