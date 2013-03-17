package net.ontrack.backend;

import net.ontrack.backend.dao.BuildDao;
import net.ontrack.backend.dao.PromotedRunDao;
import net.ontrack.backend.dao.ValidationRunDao;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.core.validation.NameDescription;
import net.ontrack.service.ControlService;
import net.ontrack.service.EventService;
import net.ontrack.service.ManagementService;
import net.ontrack.service.PropertiesService;
import net.ontrack.service.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import javax.validation.Validator;

@Service
public class ControlServiceImpl extends AbstractServiceImpl implements ControlService {

    private final ManagementService managementService;
    private final PropertiesService propertiesService;
    private final BuildDao buildDao;
    private final ValidationRunDao validationRunDao;
    private final PromotedRunDao promotedRunDao;

    @Autowired
    public ControlServiceImpl(DataSource dataSource, Validator validator, EventService auditService, ManagementService managementService, PropertiesService propertiesService, BuildDao buildDao, ValidationRunDao validationRunDao, PromotedRunDao promotedRunDao) {
        super(validator, auditService);
        this.managementService = managementService;
        this.propertiesService = propertiesService;
        this.buildDao = buildDao;
        this.validationRunDao = validationRunDao;
        this.promotedRunDao = promotedRunDao;
    }

    @Override
    @Transactional
    @Secured({SecurityRoles.CONTROLLER, SecurityRoles.ADMINISTRATOR})
    public BuildSummary createBuild(int branch, BuildCreationForm form) {
        // Validation
        validate(form, NameDescription.class);
        // Query
        int id = buildDao.createBuild(branch, form.getName(), form.getDescription());
        // Associated properties
        propertiesService.createProperties(Entity.BUILD, id, form.getProperties());
        // Branch summary
        BranchSummary theBranch = managementService.getBranch(branch);
        // Audit
        event(Event.of(EventType.BUILD_CREATED).withProject(theBranch.getProject().getId()).withBranch(theBranch.getId()).withBuild(id));
        // OK
        return managementService.getBuild(id);
    }

    @Override
    @Transactional
    @Secured({SecurityRoles.CONTROLLER, SecurityRoles.ADMINISTRATOR})
    public ValidationRunSummary createValidationRun(int build, int validationStamp, ValidationRunCreationForm validationRun) {
        // Run itself
        int validationRunId = validationRunDao.createValidationRun(
                build,
                validationStamp,
                validationRun.getDescription()
        );
        // First status
        managementService.createValidationRunStatus(
                validationRunId,
                new ValidationRunStatusCreationForm(
                        validationRun.getStatus(),
                        validationRun.getDescription()),
                true);
        // Associated properties
        propertiesService.createProperties(Entity.VALIDATION_RUN, validationRunId, validationRun.getProperties());
        // Summary
        ValidationRunSummary run = managementService.getValidationRun(validationRunId);
        // Event
        event(Event.of(EventType.VALIDATION_RUN_CREATED)
                .withProject(run.getBuild().getBranch().getProject().getId())
                .withBranch(run.getBuild().getBranch().getId())
                .withValidationStamp(validationStamp)
                .withBuild(build)
                .withValidationRun(validationRunId)
                .withValue("status", validationRun.getStatus().name())
        );
        // Gets the summary
        return run;
    }

    /**
     * Creates a promoted run for the given build and promotion level. If the promoted
     * run is already defined, returns it. If the promotion level is automatic (not
     * implemented yet), it returns null. If any other case, it creates the
     * promoted run and returns it.
     */
    @Override
    @Transactional
    @Secured({SecurityRoles.CONTROLLER, SecurityRoles.ADMINISTRATOR})
    public PromotedRunSummary createPromotedRun(int buildId, int promotionLevel, PromotedRunCreationForm promotedRun) {
        // Gets the promoted run for the build and promotion, if any
        PromotedRunSummary run = managementService.getPromotedRun(buildId, promotionLevel);
        // If none, creates one
        if (run == null) {
            // TODO Checks if the promotion level is eligible for control
            promotedRunDao.createPromotedRun(
                    buildId,
                    promotionLevel,
                    promotedRun.getDescription()
            );
            // Gets the newly created run
            run = managementService.getPromotedRun(buildId, promotionLevel);
            // Event
            event(Event.of(EventType.PROMOTED_RUN_CREATED)
                    .withProject(run.getBuild().getBranch().getProject().getId())
                    .withBranch(run.getBuild().getBranch().getId())
                    .withPromotionLevel(promotionLevel)
                    .withBuild(run.getBuild().getId())
            );
            // OK
            return run;
        }
        // If already existing, returns it
        else {
            return run;
        }
    }
}
