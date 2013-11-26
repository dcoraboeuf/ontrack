package net.ontrack.web.api.assembly;

import com.google.common.base.Function;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.web.api.model.BuildResource;

public interface BuildAssembler {

    Function<BuildSummary, BuildResource> summary();

}
