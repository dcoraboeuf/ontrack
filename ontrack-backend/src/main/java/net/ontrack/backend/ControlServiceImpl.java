package net.ontrack.backend;

import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.core.support.MapBuilder;
import net.ontrack.core.validation.NameDescription;
import net.ontrack.service.ControlService;
import net.ontrack.service.EventService;
import net.ontrack.service.ManagementService;
import net.ontrack.service.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import javax.validation.Validator;
import java.util.List;

@Service
public class ControlServiceImpl extends AbstractServiceImpl implements ControlService {

    private final ManagementService managementService;
    private final SecurityUtils securityUtils;

    @Autowired
    public ControlServiceImpl(DataSource dataSource, Validator validator, EventService auditService, ManagementService managementService, SecurityUtils securityUtils) {
        super(dataSource, validator, auditService);
        this.managementService = managementService;
        this.securityUtils = securityUtils;
    }

    @Override
    @Transactional
    @Secured({SecurityRoles.CONTROLLER, SecurityRoles.ADMINISTRATOR})
    public BuildSummary createBuild(int branch, BuildCreationForm form) {
        // Validation
        validate(form, NameDescription.class);
        // Query
        int id = dbCreate(SQL.BUILD_CREATE,
                MapBuilder.params("branch", branch)
                        .with("name", form.getName())
                        .with("description", form.getDescription()).get());
        // Branch summary
        BranchSummary theBranch = managementService.getBranch(branch);
        // Audit
        event(Event.of(EventType.BUILD_CREATED).withProject(theBranch.getProject().getId()).withBranch(theBranch.getId()).withBuild(id));
        // OK
        return new BuildSummary(id, form.getName(), form.getDescription(), theBranch);
    }

    @Override
    @Transactional
    @Secured({SecurityRoles.CONTROLLER, SecurityRoles.ADMINISTRATOR})
    public ValidationRunSummary createValidationRun(int build, int validationStamp, ValidationRunCreationForm validationRun) {
        // Numbers of runs for this build and validation stamps
        List<BuildValidationStampRun> runs = managementService.getValidationRuns(build, validationStamp);
        // Run itself
        int id = dbCreate(SQL.VALIDATION_RUN_CREATE,
                MapBuilder.params("build", build)
                        .with("validationStamp", validationStamp)
                        .with("description", validationRun.getDescription())
                        .with("runOrder", runs.size() + 1)
                        .get());
        // First status
        managementService.createValidationRunStatus(id, new ValidationRunStatusCreationForm(validationRun.getStatus(), validationRun.getDescription()), true);
        // Summary
        ValidationRunSummary run = managementService.getValidationRun(id);
        // Event
        event(Event.of(EventType.VALIDATION_RUN_CREATED)
                .withProject(run.getBuild().getBranch().getProject().getId())
                .withBranch(run.getBuild().getBranch().getId())
                .withValidationStamp(validationStamp)
                .withBuild(build)
                .withValidationRun(id)
                .withValue("status", validationRun.getStatus().name())
        );
        // Gets the summary
        return run;
    }

}
