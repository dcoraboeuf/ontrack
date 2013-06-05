package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TBuildCleanup;
import net.ontrack.core.model.Ack;
import net.ontrack.core.model.ID;

import java.util.Set;

public interface BuildCleanupDao {

    ID saveBuildCleanUp(int branch, int retention, Set<Integer> excludedPromotionLevels);

    Ack removeBuildCleanUp(int branch);

    TBuildCleanup findBuildCleanUp(int branch);

}
