package net.ontrack.backend.export;

import net.ontrack.core.model.ProjectData;
import net.ontrack.core.model.ProjectSummary;

public interface ImportService {

    ProjectSummary doImport(ProjectData projectData);

}
