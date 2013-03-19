package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TBuild;
import net.ontrack.core.model.BuildFilter;
import net.ontrack.core.model.Status;

import java.util.List;
import java.util.Set;

public interface BuildDao {

    List<TBuild> findByBranch(int branch, int offset, int count);

    TBuild getById(int id);

    int createBuild(int branch, String name, String description);

    List<TBuild> query(int branch, BuildFilter filter);

	TBuild findLastBuildWithValidationStamp(int branch, String validationStamp, Set<Status> statuses);

	TBuild findLastBuildWithPromotionLevel(int branch, String promotionLevel);

    TBuild findLastByBranch(int branch);
}
