package net.ontrack.web.api.assembly;

import com.google.common.base.Function;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.web.api.model.ProjectResource;

public interface ProjectAssembler {

    Function<ProjectSummary, ProjectResource> summary();

    Function<ProjectSummary, ProjectResource> detail();

}
