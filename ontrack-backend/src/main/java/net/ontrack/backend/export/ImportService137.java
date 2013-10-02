package net.ontrack.backend.export;

import net.ontrack.backend.dao.ProjectDao;
import net.ontrack.core.model.ProjectData;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.service.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Qualifier("1.37")
public class ImportService137 implements ImportService {

    private final ManagementService managementService;
    private final ProjectDao projectDao;

    @Autowired
    public ImportService137(ManagementService managementService, ProjectDao projectDao) {
        this.managementService = managementService;
        this.projectDao = projectDao;
    }

    @Override
    @Transactional
    public ProjectSummary doImport(ProjectData projectData) {
        // Project
        int projectId = createProject(projectData);
        // OK
        return managementService.getProject(projectId);
    }

    protected int createProject(ProjectData projectData) {
        // Reads & creates the project
        String name = projectData.getData().path("project").path("name").asText();
        String description = projectData.getData().path("project").path("description").asText();
        int projectId = projectDao.createProject(name, description);
        // TODO Branches
        // TODO Promotion levels
        // TODO Validation stamps
        // TODO Builds
        // TODO Promoted runs
        // TODO Validation runs
        // TODO Validation run statuses
        // TODO Comments
        // TODO Properties
        // TODO Build clean-up policy
        // Project ID
        return projectId;
    }

}
