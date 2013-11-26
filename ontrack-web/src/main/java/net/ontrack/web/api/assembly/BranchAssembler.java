package net.ontrack.web.api.assembly;

import com.google.common.base.Function;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.web.api.model.BranchResource;

public interface BranchAssembler {

    Function<BranchSummary, BranchResource> summary();

}
