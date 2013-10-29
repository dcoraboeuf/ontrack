package net.ontrack.backend;

import net.ontrack.backend.dao.BuildDao;
import net.ontrack.backend.dao.PromotedRunDao;
import net.ontrack.backend.dao.ValidationRunDao;
import net.ontrack.backend.security.AuthorizationUtils;
import net.ontrack.core.model.*;
import net.ontrack.core.security.ProjectFunction;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.core.support.TimeUtils;
import net.ontrack.core.validation.NameDescription;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.service.ControlService;
import net.ontrack.service.EventService;
import net.ontrack.service.ManagementService;
import net.ontrack.service.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ControlServiceImpl extends AbstractServiceImpl implements ControlService {

    private final ManagementService managementService;
    private final PropertiesService propertiesService;
    private final BuildDao buildDao;
    private final ValidationRunDao validationRunDao;
    private final PromotedRunDao promotedRunDao;
    private final SecurityUtils securityUtils;
    private final AuthorizationUtils authorizationUtils;

    @Autowired
    public ControlServiceImpl(ValidatorService validatorService, EventService auditService, ManagementService managementService, PropertiesService propertiesService, BuildDao buildDao, ValidationRunDao validationRunDao, PromotedRunDao promotedRunDao, SecurityUtils securityUtils, AuthorizationUtils authorizationUtils) {
        super(validatorService, auditService);
        this.managementService = managementService;
        this.propertiesService = propertiesService;
        this.buildDao = buildDao;
        this.validationRunDao = validationRunDao;
        this.promotedRunDao = promotedRunDao;
        this.securityUtils = securityUtils;
        this.authorizationUtils = authorizationUtils;
    }

    @Override
    @Transactional
    public BuildSummary createBuild(int branch, BuildCreationForm form) {
        // Check
        authorizationUtils.checkBranch(branch, ProjectFunction.BUILD_CREATE);
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
    public ValidationRunSummary createValidationRun(int build, int validationStamp, ValidationRunCreationForm validationRun) {
        // Check
        authorizationUtils.checkBuild(build, ProjectFunction.VALIDATION_RUN_CREATE);
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

        // Associated promotion level & auto-promotion
        PromotionLevelSummary promotionLevel = managementService.getPromotionLevelForValidationStamp(validationStamp);
        if (promotionLevel != null && promotionLevel.isAutoPromote()) {
            if (managementService.isPromotionLevelComplete(build, promotionLevel.getId())) {
                createPromotedRun(build, promotionLevel.getId(), new PromotedRunCreationForm(
                        TimeUtils.now(),
                        "Created automatically"
                ));
            }
        }

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
    public PromotedRunSummary createPromotedRun(int buildId, int promotionLevel, PromotedRunCreationForm promotedRun) {
        // Check
        authorizationUtils.checkBuild(buildId, ProjectFunction.PROMOTION_LEVEL_CREATE);
        // Gets the current signature
        Signature signature = securityUtils.getCurrentSignature();
        // If none, creates one
        promotedRunDao.createPromotedRun(
                buildId,
                promotionLevel,
                signature.getName(),
                signature.getId(),
                promotedRun.getCreation() != null ? promotedRun.getCreation() : TimeUtils.now(),
                promotedRun.getDescription()
        );
        // Loads the created run
        PromotedRunSummary run = managementService.getPromotedRun(buildId, promotionLevel);
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
}
