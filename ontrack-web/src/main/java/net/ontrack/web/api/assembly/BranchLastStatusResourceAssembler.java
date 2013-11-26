package net.ontrack.web.api.assembly;

import com.google.common.base.Function;
import net.ontrack.core.model.BranchLastStatus;
import net.ontrack.web.api.model.BranchLastStatusResource;

public interface BranchLastStatusResourceAssembler {

    Function<BranchLastStatus, BranchLastStatusResource> summary();

}
