package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import net.ontrack.backend.dao.*;
import net.ontrack.backend.dao.model.*;
import net.ontrack.backend.db.SQL;
import net.ontrack.backend.security.AuthorizationUtils;
import net.ontrack.core.model.*;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.ProjectFunction;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.core.support.MapBuilder;
import net.ontrack.core.support.TimeUtils;
import net.ontrack.core.validation.NameDescription;
import net.ontrack.extension.api.decorator.DecorationService;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.service.EventService;
import net.ontrack.service.ManagementService;
import net.ontrack.service.model.Event;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.any;

@Service
public class ManagementServiceImpl extends AbstractServiceImpl implements ManagementService {
    /**
     * Maximum number of events to store in a {@link BuildValidationStampRun}.
     *
     * @see #getValidationRuns(java.util.Locale, int, int)
     */
    public static final int MAX_EVENTS_IN_BUILD_VALIDATION_STAMP_RUN = 10;
    // TODO Split the service in different parts
    private final SecurityUtils securityUtils;
    private final AuthorizationUtils authorizationUtils;
    private final Strings strings;
    private final AccountDao accountDao;
    private final ProjectDao projectDao;
    private final BranchDao branchDao;
    private final ValidationStampDao validationStampDao;
    private final PromotionLevelDao promotionLevelDao;
    private final BuildDao buildDao;
    private final PromotedRunDao promotedRunDao;
    private final ValidationRunDao validationRunDao;
    private final ValidationRunStatusDao validationRunStatusDao;
    private final ValidationRunEventDao validationRunEventDao;
    private final CommentDao commentDao;
    private final EntityDao entityDao;
    private final BuildCleanupDao buildCleanupDao;
    private final DashboardDao dashboardDao;
    private final PropertiesService propertiesService;
    private final DecorationService decorationService;
    // Dao -> Summary converters
    private final Function<TProject, ProjectSummary> projectSummaryFunction = new Function<TProject, ProjectSummary>() {
        @Override
        public ProjectSummary apply(TProject t) {
            return new ProjectSummary(t.getId(), t.getName(), t.getDescription());
        }
    };
    private final Function<TBranch, BranchSummary> branchSummaryFunction = new Function<TBranch, BranchSummary>() {
        @Override
        public BranchSummary apply(TBranch t) {
            return new BranchSummary(
                    t.getId(),
                    t.getName(),
                    t.getDescription(),
                    getProject(t.getProject())
            );
        }
    };
    private final Function<TValidationStamp, ValidationStampSummary> validationStampSummaryFunction = new Function<TValidationStamp, ValidationStampSummary>() {
        @Override
        public ValidationStampSummary apply(TValidationStamp t) {
            return new ValidationStampSummary(
                    t.getId(),
                    t.getName(),
                    t.getDescription(),
                    getBranch(t.getBranch()),
                    t.getOrderNb(),
                    getAccountSummary(t.getOwnerId())
            );
        }
    };
    private final Function<TPromotionLevel, PromotionLevelSummary> promotionLevelSummaryFunction = new Function<TPromotionLevel, PromotionLevelSummary>() {
        @Override
        public PromotionLevelSummary apply(TPromotionLevel t) {
            return new PromotionLevelSummary(
                    t.getId(),
                    getBranch(t.getBranch()),
                    t.getLevelNb(),
                    t.getName(),
                    t.getDescription(),
                    t.isAutoPromote()
            );
        }
    };
    private final Function<TBuild, BuildSummary> buildSummaryFunction = new Function<TBuild, BuildSummary>() {

        @Override
        public BuildSummary apply(TBuild t) {
            return new BuildSummary(
                    t.getId(),
                    t.getName(),
                    t.getDescription(),
                    getBranch(t.getBranch())
            );
        }
    };
    private final Function<PromotionLevelSummary, PromotionLevelAndStamps> promotionLevelAndStampsFunction = new Function<PromotionLevelSummary, PromotionLevelAndStamps>() {
        @Override
        public PromotionLevelAndStamps apply(PromotionLevelSummary promotionLevelSummary) {
            // Gets the list of stamps for this promotion level
            List<ValidationStampSummary> stamps = getValidationStampForPromotionLevel(promotionLevelSummary.getId());
            // OK
            return new PromotionLevelAndStamps(promotionLevelSummary).withStamps(stamps);
        }
    };

    @Autowired
    public ManagementServiceImpl(
            ValidatorService validatorService,
            EventService auditService,
            SecurityUtils securityUtils,
            AuthorizationUtils authorizationUtils,
            Strings strings,
            AccountDao accountDao,
            ProjectDao projectDao,
            BranchDao branchDao,
            ValidationStampDao validationStampDao,
            PromotionLevelDao promotionLevelDao,
            BuildDao buildDao,
            PromotedRunDao promotedRunDao,
            ValidationRunDao validationRunDao,
            ValidationRunStatusDao validationRunStatusDao,
            ValidationRunEventDao validationRunEventDao,
            CommentDao commentDao,
            EntityDao entityDao,
            BuildCleanupDao buildCleanupDao,
            DashboardDao dashboardDao,
            PropertiesService propertiesService,
            DecorationService decorationService
    ) {
        super(validatorService, auditService);
        this.securityUtils = securityUtils;
        this.authorizationUtils = authorizationUtils;
        this.strings = strings;
        this.accountDao = accountDao;
        this.projectDao = projectDao;
        this.branchDao = branchDao;
        this.validationStampDao = validationStampDao;
        this.promotionLevelDao = promotionLevelDao;
        this.buildDao = buildDao;
        this.promotedRunDao = promotedRunDao;
        this.validationRunDao = validationRunDao;
        this.validationRunStatusDao = validationRunStatusDao;
        this.validationRunEventDao = validationRunEventDao;
        this.commentDao = commentDao;
        this.entityDao = entityDao;
        this.buildCleanupDao = buildCleanupDao;
        this.dashboardDao = dashboardDao;
        this.propertiesService = propertiesService;
        this.decorationService = decorationService;
    }

    private AccountSummary getAccountSummary(Integer id) {
        if (id == null) {
            return null;
        } else {
            TAccount account = accountDao.getByID(id);
            return new AccountSummary(
                    account.getId(),
                    account.getName(),
                    account.getFullName()
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectSummary> getProjectList() {
        return Lists.transform(
                projectDao.findAll(),
                projectSummaryFunction
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectSummary getProject(int id) {
        return projectSummaryFunction.apply(projectDao.getById(id));
    }

    @Override
    @Transactional
    public ProjectSummary createProject(ProjectCreationForm form) {
        authorizationUtils.checkGlobal(GlobalFunction.PROJECT_CREATE);
        // Validation
        validate(form, NameDescription.class);
        // Query
        int id = projectDao.createProject(form.getName(), form.getDescription());
        // Audit
        event(Event.of(EventType.PROJECT_CREATED).withProject(id));
        // OK
        return new ProjectSummary(id, form.getName(), form.getDescription());
    }

    @Override
    @Transactional
    public ProjectSummary updateProject(int id, ProjectUpdateForm form) {
        authorizationUtils.checkProject(id, ProjectFunction.PROJECT_MODIFY);
        // Validation
        validate(form, NameDescription.class);
        // Query
        Ack ack = projectDao.updateProject(id, form.getName(), form.getDescription());
        // Audit
        if (ack.isSuccess()) {
            event(Event.of(EventType.PROJECT_UPDATED).withProject(id));
        }
        // OK
        return getProject(id);
    }

    @Override
    @Transactional
    public Ack updateProjectValidationStamps(int projectId, ProjectValidationStampMgt form) {
        authorizationUtils.checkProject(projectId, ProjectFunction.PROMOTION_LEVEL_MGT);
        // Gets the branch by name
        Map<Entity, Integer> projectIdMap = Collections.singletonMap(Entity.PROJECT, projectId);
        int branch1id = entityDao.getEntityId(Entity.BRANCH, form.getBranch1(), projectIdMap);
        int branch2id = entityDao.getEntityId(Entity.BRANCH, form.getBranch2(), projectIdMap);
        // For all the validation stamp names
        for (String stampName : form.getStamps()) {
            // Gets the validation stamp for the branch 1
            TValidationStamp stamp1 = validationStampDao.getByBranchAndName(branch1id, stampName);
            // Gets the validation stamp for the branch 2
            TValidationStamp stamp2 = validationStampDao.findByBranchAndName(branch2id, stampName);
            // If the stamp does not exist, create it
            if (stamp2 == null) {
                cloneValidationStampSummary(branch2id, stamp1, form.getReplacements());
            }
            // If it exists, sync. the properties
            else {
                replaceProperties(form.getReplacements(), Entity.VALIDATION_STAMP, stamp1.getId(), stamp2.getId());
            }
        }
        // OK
        return Ack.OK;
    }

    // Validation stamps

    @Override
    @Transactional
    public Ack deleteProject(int id) {
        authorizationUtils.checkProject(id, ProjectFunction.PROJECT_DELETE);
        String name = getEntityName(Entity.PROJECT, id);
        Ack ack = projectDao.deleteProject(id);
        if (ack.isSuccess()) {
            event(Event.of(EventType.PROJECT_DELETED).withValue("project", name));
        }
        return ack;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchSummary> getBranchList(int project) {
        return Lists.transform(
                branchDao.findByProject(project),
                branchSummaryFunction
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BranchSummary getBranch(int id) {
        return branchSummaryFunction.apply(branchDao.getById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public BranchLastStatus getBranchLastStatus(final Locale locale, int id) {
        // Gets the list of promotions
        List<PromotionLevelSummary> promotionLevelList = getPromotionLevelList(id);
        promotionLevelList = new ArrayList<>(promotionLevelList);
        Collections.reverse(promotionLevelList);
        List<Promotion> lastPromotions = Lists.transform(
                promotionLevelList,
                new Function<PromotionLevelSummary, Promotion>() {
                    @Override
                    public Promotion apply(PromotionLevelSummary promotionLevel) {
                        return findLastPromotion(locale, promotionLevel.getId());
                    }
                }
        );
        // OK
        return new BranchLastStatus(
                getBranch(id),
                getLastBuild(id),
                lastPromotions
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DecoratedBranch getDecoratedBranch(Locale locale, int branchId) {
        BranchSummary branch = getBranch(branchId);
        return new DecoratedBranch(
                branch,
                getLocalizedDecorations(locale, Entity.BRANCH, branchId)
        );
    }

    @Override
    @Transactional
    public BranchSummary createBranch(int project, BranchCreationForm form) {
        authorizationUtils.checkProject(project, ProjectFunction.BRANCH_CREATE);
        // Validation
        validate(form, NameDescription.class);
        // Query
        int id = branchDao.createBranch(
                project,
                form.getName(),
                form.getDescription()
        );
        // Audit
        event(Event.of(EventType.BRANCH_CREATED).withProject(project).withBranch(id));
        // OK
        return new BranchSummary(id, form.getName(), form.getDescription(), getProject(project));
    }

    @Override
    @Transactional
    public Ack deleteBranch(int branchId) {
        authorizationUtils.checkBranch(branchId, ProjectFunction.BRANCH_DELETE);
        BranchSummary branch = getBranch(branchId);
        Ack ack = branchDao.deleteBranch(branchId);
        if (ack.isSuccess()) {
            event(Event.of(EventType.BRANCH_DELETED)
                    .withValue("project", branch.getProject().getName())
                    .withValue("branch", branch.getName()));
        }
        return ack;
    }

    @Override
    @Transactional
    public BranchSummary updateBranch(int branch, BranchUpdateForm form) {
        authorizationUtils.checkBranch(branch, ProjectFunction.BRANCH_MODIFY);
        // Validation
        validate(form, NameDescription.class);
        // Loads existing branch
        BranchSummary existingBranch = getBranch(branch);
        // Query
        branchDao.updateBranch(
                branch,
                form.getName(),
                form.getDescription()
        );
        // Audit
        event(Event.of(EventType.BRANCH_UPDATED).withProject(existingBranch.getProject().getId()).withBranch(branch));
        // OK
        return getBranch(branch);
    }

    @Override
    @Transactional
    public BranchSummary cloneBranch(int branchId, BranchCloneForm form) {
        authorizationUtils.checkBranch(branchId, ProjectFunction.BRANCH_CLONE);
        // Validation
        validate(form, NameDescription.class);
        // Gets the original branch
        BranchSummary originalBranch = getBranch(branchId);
        // Creates the (empty) branch
        BranchSummary newBranch = createBranch(
                originalBranch.getProject().getId(),
                new BranchCreationForm(
                        form.getName(),
                        form.getDescription()
                )
        );
        int newBranchId = newBranch.getId();

        // Build clean-up configuration
        TBuildCleanup cleanup = buildCleanupDao.findBuildCleanUp(branchId);
        Set<Integer> newCleanupExcludedPromotionLevels = new HashSet<>();

        // Branch properties
        propertiesService.createProperties(
                Entity.BRANCH,
                newBranchId,
                new PropertiesCreationForm(Lists.newArrayList(form.getBranchProperties()))
        );

        // Promotion levels
        List<PromotionLevelSummary> promotionLevelList = new ArrayList<>(getPromotionLevelList(branchId));
        // Sort by increasing level number
        Collections.sort(
                promotionLevelList,
                new Comparator<PromotionLevelSummary>() {
                    @Override
                    public int compare(PromotionLevelSummary o1, PromotionLevelSummary o2) {
                        return o1.getLevelNb() - o2.getLevelNb();
                    }
                }
        );


        // Links between validation stamps & promotion levels
        Map<String, Integer> links = new HashMap<>();

        // Cloning the promotion levels
        for (PromotionLevelSummary promotionLevel : promotionLevelList) {

            // Creates the new promotion level
            PromotionLevelSummary newPromotionLevel = createPromotionLevel(
                    newBranchId,
                    new PromotionLevelCreationForm(
                            promotionLevel.getName(),
                            promotionLevel.getDescription()
                    )
            );

            // Clean-up configuration
            if (cleanup != null && cleanup.getExcludedPromotionLevels().contains(promotionLevel.getId())) {
                newCleanupExcludedPromotionLevels.add(newPromotionLevel.getId());
            }

            // Promotion level properties
            replaceProperties(form.getPromotionLevelReplacements(), Entity.PROMOTION_LEVEL, promotionLevel.getId(), newPromotionLevel.getId());

            // Copies any image
            byte[] promotionLevelImage = imagePromotionLevel(promotionLevel.getId());
            if (promotionLevelImage != null) {
                promotionLevelDao.updateImage(
                        newPromotionLevel.getId(),
                        promotionLevelImage);
            }

            // Gets all the linked stamps
            List<TValidationStamp> linkedStamps = validationStampDao.findByPromotionLevel(promotionLevel.getId());
            for (TValidationStamp linkedStamp : linkedStamps) {
                links.put(linkedStamp.getName(), newPromotionLevel.getId());
            }
        }

        // Gets all the validation stamps
        Map<Integer, Integer> stampMapping = new HashMap<>();
        List<TValidationStamp> stamps = validationStampDao.findByBranch(branchId);
        for (TValidationStamp stamp : stamps) {
            // Clones the validation stamp
            ValidationStampSummary newValidationStamp = cloneValidationStampSummary(newBranchId, stamp, form.getValidationStampReplacements());
            stampMapping.put(stamp.getId(), newValidationStamp.getId());
            // Link?
            Integer linkedPromotionLevel = links.get(stamp.getName());
            if (linkedPromotionLevel != null) {
                linkValidationStampToPromotionLevel(newValidationStamp.getId(), linkedPromotionLevel);
            }
        }

        // Saves the clean-up configuration
        if (cleanup != null) {
            buildCleanupDao.saveBuildCleanUp(newBranchId, cleanup.getRetention(), newCleanupExcludedPromotionLevels);
        }

        // Dashboard configuration
        for (Map.Entry<Integer, Integer> stamp : stampMapping.entrySet()) {
            int oldId = stamp.getKey();
            int newId = stamp.getValue();
            if (dashboardDao.isValidationStampSelectedForBranch(oldId, branchId)) {
                dashboardDao.associateBranchValidationStamp(newBranchId, newId);
            }
        }

        // OK
        return newBranch;
    }

    @Override
    @Transactional
    public BuildCleanup getBuildCleanup(int branchId) {
        authorizationUtils.checkBranch(branchId, ProjectFunction.BUILD_CLEANUP_CONFIG);
        // List of promotion levels
        List<PromotionLevelSummary> promotionLevelList = getPromotionLevelList(branchId);
        // No exclusion by default
        List<FlaggedPromotionLevel> flaggedPromotionLevels = Lists.transform(
                promotionLevelList,
                FlaggedPromotionLevel.UNFLAGGED
        );
        // Gets the configuration
        final TBuildCleanup conf = buildCleanupDao.findBuildCleanUp(branchId);
        if (conf != null) {
            // Flags
            flaggedPromotionLevels = Lists.transform(
                    flaggedPromotionLevels,
                    FlaggedPromotionLevel.selectFn(new Predicate<PromotionLevelSummary>() {
                        @Override
                        public boolean apply(PromotionLevelSummary summary) {
                            return conf.getExcludedPromotionLevels().contains(summary.getId());
                        }
                    })
            );
            // OK
            return new BuildCleanup(
                    conf.getRetention(),
                    flaggedPromotionLevels
            );
        } else {
            return new BuildCleanup(
                    0,
                    flaggedPromotionLevels
            );
        }
    }

    @Override
    @Transactional
    public Ack setBuildCleanup(int branchId, BuildCleanupForm form) {
        authorizationUtils.checkBranch(branchId, ProjectFunction.BUILD_CLEANUP_CONFIG);
        if (form.getRetention() <= 0) {
            return buildCleanupDao.removeBuildCleanUp(branchId);
        } else {
            return buildCleanupDao.saveBuildCleanUp(
                    branchId,
                    form.getRetention(),
                    form.getExcludedPromotionLevels()
            ).ack();
        }
    }

    private String applyReplacement(String regex, String replacement, String value) {
        if (value == null) {
            return null;
        } else {
            return value.replaceAll(regex, replacement);
        }
    }

    private PropertyReplacement getPropertyReplacement(Collection<PropertyReplacement> replacements, final String extension, final String name) {
        return Iterables.find(
                replacements,
                new Predicate<PropertyReplacement>() {
                    @Override
                    public boolean apply(PropertyReplacement replacement) {
                        return StringUtils.equals(extension, replacement.getExtension())
                                && StringUtils.equals(name, replacement.getName());
                    }
                },
                null
        );
    }

    private ValidationStampSummary cloneValidationStampSummary(int newBranchId, TValidationStamp oldStamp, Collection<PropertyReplacement> replacements) {
        ValidationStampSummary newValidationStamp = createValidationStamp(
                newBranchId,
                new ValidationStampCreationForm(
                        oldStamp.getName(),
                        oldStamp.getDescription()
                )
        );

        // Validation stamp properties
        replaceProperties(replacements, Entity.VALIDATION_STAMP, oldStamp.getId(), newValidationStamp.getId());

        // Copies any image
        byte[] image = imageValidationStamp(oldStamp.getId());
        if (image != null) {
            validationStampDao.updateImage(
                    newValidationStamp.getId(),
                    image);
        }
        return newValidationStamp;
    }

    private void replaceProperties(Collection<PropertyReplacement> replacements, Entity entity, int oldEntityId, int newEntityId) {
        List<PropertyValue> oldProperties = propertiesService.getPropertyValues(entity, oldEntityId);
        for (PropertyValue oldProperty : oldProperties) {
            String extension = oldProperty.getExtension();
            String name = oldProperty.getName();
            String value = oldProperty.getValue();
            // Gets any replacement
            PropertyReplacement replacement = getPropertyReplacement(replacements, extension, name);
            if (replacement != null) {
                // Applies the replacement
                value = applyReplacement(replacement.getRegex(), replacement.getReplacement(), value);
            }
            // Creates the property if not empty
            if (StringUtils.isNotBlank(value)) {
                propertiesService.createProperties(entity, newEntityId,
                        new PropertiesCreationForm(
                                Arrays.asList(
                                        new PropertyCreationForm(
                                                extension,
                                                name,
                                                value
                                        )
                                )
                        ));
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ValidationStampSummary> getValidationStampList(int branch) {
        return Lists.transform(
                validationStampDao.findByBranch(branch),
                validationStampSummaryFunction
        );
    }

    // Promotion levels

    @Override
    @Transactional(readOnly = true)
    public ValidationStampSummary getValidationStamp(int id) {
        return validationStampSummaryFunction.apply(
                validationStampDao.getById(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DecoratedValidationStamp getDecoratedValidationStamp(Locale locale, int validationStampId) {
        ValidationStampSummary validationStamp = getValidationStamp(validationStampId);
        return new DecoratedValidationStamp(
                validationStamp,
                getLocalizedDecorations(locale, Entity.VALIDATION_STAMP, validationStampId)
        );
    }

    @Override
    @Transactional
    public ValidationStampSummary createValidationStamp(int branch, ValidationStampCreationForm form) {
        authorizationUtils.checkBranch(branch, ProjectFunction.VALIDATION_STAMP_CREATE);
        // Validation
        validate(form, NameDescription.class);
        // Query
        int id = validationStampDao.createValidationStamp(branch, form.getName(), form.getDescription());
        // Branch summary
        BranchSummary theBranch = getBranch(branch);
        // Audit
        event(Event.of(EventType.VALIDATION_STAMP_CREATED)
                .withProject(theBranch.getProject().getId())
                .withBranch(theBranch.getId())
                .withValidationStamp(id));
        // OK
        return getValidationStamp(id);
    }

    @Override
    @Transactional
    public ValidationStampSummary updateValidationStamp(int validationStampId, ValidationStampUpdateForm form) {
        authorizationUtils.checkValidationStamp(validationStampId, ProjectFunction.VALIDATION_STAMP_MODIFY);
        // Validation
        validate(form, NameDescription.class);
        // Existing value
        ValidationStampSummary existing = getValidationStamp(validationStampId);
        // Query
        validationStampDao.updateValidationStamp(validationStampId, form.getName(), form.getDescription());
        // Audit
        event(Event.of(EventType.VALIDATION_STAMP_UPDATED)
                .withProject(existing.getBranch().getProject().getId())
                .withBranch(existing.getBranch().getId())
                .withValidationStamp(validationStampId));
        // OK
        return getValidationStamp(validationStampId);
    }

    @Override
    @Transactional
    public Ack deleteValidationStamp(int validationStampId) {
        authorizationUtils.checkValidationStamp(validationStampId, ProjectFunction.VALIDATION_STAMP_DELETE);
        ValidationStampSummary validationStamp = getValidationStamp(validationStampId);
        Ack ack = validationStampDao.deleteValidationStamp(validationStampId);
        if (ack.isSuccess()) {
            event(Event.of(EventType.VALIDATION_STAMP_DELETED)
                    .withValue("project", validationStamp.getBranch().getProject().getName())
                    .withValue("branch", validationStamp.getBranch().getName())
                    .withValue("validationStamp", validationStamp.getName()));
        }
        return ack;
    }

    @Override
    @Transactional
    public Ack upValidationStamp(int validationStampId) {
        authorizationUtils.checkValidationStamp(validationStampId, ProjectFunction.VALIDATION_STAMP_MODIFY);
        return validationStampDao.upValidationStamp(validationStampId);
    }

    @Override
    @Transactional
    public Ack downValidationStamp(int validationStampId) {
        authorizationUtils.checkValidationStamp(validationStampId, ProjectFunction.VALIDATION_STAMP_MODIFY);
        return validationStampDao.downValidationStamp(validationStampId);
    }

    @Override
    @Transactional
    public Ack moveValidationStamp(int validationStampId, int newIndex) {
        authorizationUtils.checkValidationStamp(validationStampId, ProjectFunction.VALIDATION_STAMP_MODIFY);
        return validationStampDao.moveValidationStamp(validationStampId, newIndex);
    }

    @Override
    @Transactional
    public Ack setValidationStampOwner(int validationStampId, int ownerId) {
        authorizationUtils.checkValidationStamp(validationStampId, ProjectFunction.VALIDATION_STAMP_MODIFY);
        return validationStampDao.setValidationStampOwner(validationStampId, ownerId);
    }

    @Override
    @Transactional
    public Ack unsetValidationStampOwner(int validationStampId) {
        authorizationUtils.checkValidationStamp(validationStampId, ProjectFunction.VALIDATION_STAMP_MODIFY);
        return validationStampDao.setValidationStampOwner(validationStampId, null);
    }

    @Override
    @Transactional
    public Ack addValidationStampComment(int validationStampId, ValidationStampCommentForm form) {
        securityUtils.checkIsLogged();
        // Comment
        CommentStub comment = createComment(Entity.VALIDATION_STAMP, validationStampId, form.getComment());
        // Registers an event for this comment
        event(
                collectEntityContext(
                        Event.of(EventType.VALIDATION_STAMP_COMMENT), Entity.VALIDATION_STAMP, validationStampId)
                        .withEntity(Entity.VALIDATION_STAMP, validationStampId)
                        .withComment(comment.getComment()));
        // OK
        return Ack.OK;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Comment> getValidationStampComments(final Locale locale, final int validationStampId, int offset, int count) {
        return Lists.newArrayList(Collections2.transform(
                commentDao.findByEntity(Entity.VALIDATION_STAMP, validationStampId, offset, count),
                new Function<TComment, Comment>() {
                    @Override
                    public Comment apply(TComment t) {
                        return new Comment(
                                t.getId(),
                                t.getContent(),
                                new DatedSignature(
                                        new Signature(
                                                t.getAuthorId(),
                                                t.getAuthor()
                                        ),
                                        t.getTimestamp(),
                                        TimeUtils.elapsed(strings, locale, t.getTimestamp(), TimeUtils.now(), t.getAuthor()),
                                        TimeUtils.format(locale, t.getTimestamp())
                                )
                        );
                    }
                }
        ));
    }

    @Override
    @Transactional
    public Ack imageValidationStamp(final int validationStampId, MultipartFile image) {
        authorizationUtils.checkValidationStamp(validationStampId, ProjectFunction.VALIDATION_STAMP_MODIFY);
        return setImage(
                image,
                SQL.VALIDATION_STAMP_IMAGE_MAXSIZE,
                new Function<byte[], Ack>() {
                    @Override
                    public Ack apply(byte[] image) {
                        return validationStampDao.updateImage(validationStampId, image);
                    }
                });

    }

    @Override
    public byte[] imageValidationStamp(int validationStampId) {
        return validationStampDao.getImage(validationStampId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionLevelSummary> getPromotionLevelList(int branch) {
        return Lists.transform(
                promotionLevelDao.findByBranch(branch),
                promotionLevelSummaryFunction
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionLevelSummary getPromotionLevel(int promotionLevelId) {
        return promotionLevelSummaryFunction.apply(
                promotionLevelDao.getById(promotionLevelId)
        );
    }

    @Override
    @Transactional
    public PromotionLevelSummary createPromotionLevel(int branchId, PromotionLevelCreationForm form) {
        authorizationUtils.checkBranch(branchId, ProjectFunction.PROMOTION_LEVEL_CREATE);
        // Validation
        validate(form, NameDescription.class);
        // Query
        int id = promotionLevelDao.createPromotionLevel(
                branchId,
                form.getName(),
                form.getDescription()
        );
        // Branch summary
        BranchSummary theBranch = getBranch(branchId);
        // Audit
        event(Event.of(EventType.PROMOTION_LEVEL_CREATED)
                .withProject(theBranch.getProject().getId())
                .withBranch(theBranch.getId())
                .withPromotionLevel(id));
        // OK
        return getPromotionLevel(id);
    }

    @Override
    @Transactional
    public PromotionLevelSummary updatePromotionLevel(int promotionLevelId, PromotionLevelUpdateForm form) {
        authorizationUtils.checkPromotionLevel(promotionLevelId, ProjectFunction.PROMOTION_LEVEL_MODIFY);
        // Validation
        validate(form, NameDescription.class);
        // Existing value
        PromotionLevelSummary existing = getPromotionLevel(promotionLevelId);
        // Query
        promotionLevelDao.updatePromotionLevel(promotionLevelId, form.getName(), form.getDescription());
        // Audit
        event(Event.of(EventType.PROMOTION_LEVEL_UPDATED)
                .withProject(existing.getBranch().getProject().getId())
                .withBranch(existing.getBranch().getId())
                .withPromotionLevel(promotionLevelId));
        // OK
        return getPromotionLevel(promotionLevelId);
    }

    @Override
    @Transactional
    public Ack deletePromotionLevel(int promotionLevelId) {
        authorizationUtils.checkPromotionLevel(promotionLevelId, ProjectFunction.PROMOTION_LEVEL_DELETE);
        PromotionLevelSummary promotionLevel = getPromotionLevel(promotionLevelId);
        Ack ack = promotionLevelDao.deletePromotionLevel(promotionLevelId);
        if (ack.isSuccess()) {
            event(Event.of(EventType.PROMOTION_LEVEL_DELETED)
                    .withValue("project", promotionLevel.getBranch().getProject().getName())
                    .withValue("branch", promotionLevel.getBranch().getName())
                    .withValue("promotionLevel", promotionLevel.getName()));
        }
        return ack;
    }

    @Override
    @Transactional
    public Ack linkValidationStampToPromotionLevel(int validationStampId, int promotionLevelId) {
        authorizationUtils.checkValidationStamp(validationStampId, ProjectFunction.PROMOTION_LEVEL_MGT);
        Ack ack = validationStampDao.linkValidationStampToPromotionLevel(validationStampId, promotionLevelId);
        if (ack.isSuccess()) {
            Event event = Event.of(EventType.VALIDATION_STAMP_LINKED);
            event = collectEntityContext(event, Entity.VALIDATION_STAMP, validationStampId);
            event = collectEntityContext(event, Entity.PROMOTION_LEVEL, promotionLevelId);
            event(event);
            return Ack.OK;
        } else {
            return Ack.NOK;
        }
    }

    @Override
    @Transactional
    public Ack unlinkValidationStampToPromotionLevel(int validationStampId) {
        authorizationUtils.checkValidationStamp(validationStampId, ProjectFunction.PROMOTION_LEVEL_MGT);
        Ack ack = validationStampDao.unlinkValidationStampToPromotionLevel(validationStampId);
        if (ack.isSuccess()) {
            Event event = Event.of(EventType.VALIDATION_STAMP_UNLINKED);
            event = collectEntityContext(event, Entity.VALIDATION_STAMP, validationStampId);
            event(event);
            return Ack.OK;
        } else {
            return Ack.NOK;
        }
    }

    @Override
    @Transactional
    public Ack upPromotionLevel(int promotionLevelId) {
        authorizationUtils.checkPromotionLevel(promotionLevelId, ProjectFunction.PROMOTION_LEVEL_MGT);
        return promotionLevelDao.upPromotionLevel(promotionLevelId);
    }

    @Override
    @Transactional
    public Ack downPromotionLevel(int promotionLevelId) {
        authorizationUtils.checkPromotionLevel(promotionLevelId, ProjectFunction.PROMOTION_LEVEL_MGT);
        return promotionLevelDao.downPromotionLevel(promotionLevelId);
    }

    @Override
    @Transactional
    public Ack imagePromotionLevel(final int promotionLevelId, MultipartFile image) {
        authorizationUtils.checkPromotionLevel(promotionLevelId, ProjectFunction.PROMOTION_LEVEL_MODIFY);
        return setImage(
                image,
                SQL.PROMOTION_LEVEL_IMAGE_MAXSIZE,
                new Function<byte[], Ack>() {
                    @Override
                    public Ack apply(byte[] image) {
                        return promotionLevelDao.updateImage(promotionLevelId, image);
                    }
                });
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] imagePromotionLevel(int promotionLevelId) {
        return promotionLevelDao.getImage(promotionLevelId);
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionLevelManagementData getPromotionLevelManagementData(int branchId) {
        authorizationUtils.checkBranch(branchId, ProjectFunction.PROMOTION_LEVEL_MGT);
        // Gets the branch
        BranchSummary branch = getBranch(branchId);
        // List of validation stamps for this branch, without any promotion level
        List<ValidationStampSummary> freeValidationStampList = getValidationStampWithoutPromotionLevel(branchId);
        // List of promotion levels for this branch
        List<PromotionLevelSummary> promotionLevelList = getPromotionLevelList(branchId);
        // List of promotion levels with stamps
        List<PromotionLevelAndStamps> promotionLevelAndStampsList = Lists.transform(promotionLevelList, promotionLevelAndStampsFunction);
        // OK
        return new PromotionLevelManagementData(branch, freeValidationStampList, promotionLevelAndStampsList);
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionLevelAndStamps getPromotionLevelValidationStamps(int promotionLevelId) {
        return promotionLevelAndStampsFunction.apply(getPromotionLevel(promotionLevelId));
    }

    @Override
    @Transactional
    public Flag setPromotionLevelAutoPromote(int promotionLevelId) {
        authorizationUtils.checkPromotionLevel(promotionLevelId, ProjectFunction.PROMOTION_LEVEL_MGT);
        // Auto promotion can be enabled only if the promotion level is associated to at least one validation stamp
        List<TValidationStamp> stamps = validationStampDao.findByPromotionLevel(promotionLevelId);
        if (stamps.isEmpty()) {
            return Flag.UNSET;
        } else {
            promotionLevelDao.setAutoPromote(promotionLevelId, true);
            return Flag.SET;
        }
    }

    @Override
    @Transactional
    public Flag unsetPromotionLevelAutoPromote(int promotionLevelId) {
        authorizationUtils.checkPromotionLevel(promotionLevelId, ProjectFunction.PROMOTION_LEVEL_MGT);
        promotionLevelDao.setAutoPromote(promotionLevelId, false);
        return Flag.UNSET;
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionLevelSummary getPromotionLevelForValidationStamp(int validationStamp) {
        TValidationStamp t = validationStampDao.getById(validationStamp);
        Integer promotionLevelId = t.getPromotionLevel();
        if (promotionLevelId != null) {
            return getPromotionLevel(promotionLevelId);
        } else {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPromotionLevelComplete(final int build, int promotionLevelId) {
        // Gets the promotion level
        PromotionLevelSummary promotionLevel = getPromotionLevel(promotionLevelId);
        if (!promotionLevel.isAutoPromote()) {
            return false;
        } else {
            // Gets the list of validation stamps for this promotion level
            List<ValidationStampSummary> stamps = getValidationStampForPromotionLevel(promotionLevelId);
            return Iterables.all(
                    stamps,
                    new Predicate<ValidationStampSummary>() {
                        @Override
                        public boolean apply(ValidationStampSummary stamp) {
                            TValidationRun r = validationRunDao.findLastByBuildAndValidationStamp(build, stamp.getId());
                            if (r != null) {
                                TValidationRunStatus rs = validationRunStatusDao.findLastForValidationRun(r.getId());
                                return rs != null && rs.getStatus() == Status.PASSED;
                            } else {
                                return false;
                            }
                        }
                    }
            );
        }
    }

    protected List<ValidationStampSummary> getValidationStampForPromotionLevel(int promotionLevelId) {
        return Lists.transform(
                validationStampDao.findByPromotionLevel(promotionLevelId),
                validationStampSummaryFunction
        );
    }

    protected List<ValidationStampSummary> getValidationStampWithoutPromotionLevel(int branchId) {
        return Lists.transform(
                validationStampDao.findByNoPromotionLevel(branchId),
                validationStampSummaryFunction
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BranchBuilds getBuildList(final Locale locale, int branch, int offset, int count) {
        return getBranchBuilds(locale, branch, buildDao.findByBranch(branch, offset, count));
    }

    @Override
    @Transactional(readOnly = true)
    public BranchBuilds queryBuilds(final Locale locale, int branch, BuildFilter filter) {
        return getBranchBuilds(locale, branch, buildDao.query(branch, filter));
    }

    @Override
    public BuildSummary findLastBuildWithValidationStamp(int validationStamp, Set<Status> statuses) {
        TBuild tBuild = buildDao.findLastBuildWithValidationStamp(validationStamp, statuses);
        if (tBuild != null) {
            return buildSummaryFunction.apply(tBuild);
        } else {
            return null;
        }
    }

    @Override
    public BuildSummary findLastBuildWithPromotionLevel(final int promotionLevel) {
        TBuild tBuild = buildDao.findLastBuildWithPromotionLevel(promotionLevel);
        if (tBuild != null) {
            return buildSummaryFunction.apply(tBuild);
        } else {
            return null;
        }
    }

    private BranchBuilds getBranchBuilds(final Locale locale, int branch, List<TBuild> tlist) {
        return new BranchBuilds(
                // Validation stamps for the branch
                Lists.transform(
                        getValidationStampList(branch),
                        ValidationStampSummary.toValidationStampFn
                ),
                // Promotion levels for the branch
                Lists.transform(
                        getPromotionLevelList(branch),
                        PromotionLevelSummary.toPromotionLevelFn
                ),
                // Status list
                Arrays.asList(Status.values()),
                // Builds for the branch and their complete status
                Lists.transform(
                        tlist,
                        getBuildCompleteStatusFn(locale)
                )
        );
    }

    private Function<TBuild, BranchBuild> getBuildCompleteStatusFn(final Locale locale) {
        return new Function<TBuild, BranchBuild>() {
            @Override
            public BranchBuild apply(TBuild t) {
                int buildId = t.getId();
                List<LocalizedDecoration> decorations = getLocalizedDecorations(locale, Entity.BUILD, buildId);
                List<BranchBuildValidationStampLastStatus> stamps = Lists.transform(
                        getBuildValidationStamps(locale, buildId),
                        new Function<BuildValidationStamp, BranchBuildValidationStampLastStatus>() {
                            @Override
                            public BranchBuildValidationStampLastStatus apply(BuildValidationStamp buildValidationStamp) {
                                // Gets the last validation run
                                BranchBuildLastValidationRun lastValidationRun = null;
                                List<BuildValidationStampRun> runs = buildValidationStamp.getRuns();
                                if (runs != null && !runs.isEmpty()) {
                                    BuildValidationStampRun run = runs.get(runs.size() - 1);
                                    ValidationRunStatusStub lastValidationRunStatus = getLastValidationRunStatus(run.getRunId());
                                    lastValidationRun = new BranchBuildLastValidationRun(
                                            run.getRunId(),
                                            run.getRunOrder(),
                                            run.getSignature(),
                                            new BranchBuildLastValidationRunStatus(
                                                    lastValidationRunStatus.getId(),
                                                    lastValidationRunStatus.getStatus(),
                                                    lastValidationRunStatus.getDescription()
                                            )
                                    );
                                }
                                // OK
                                return new BranchBuildValidationStampLastStatus(
                                        buildValidationStamp.getValidationStampId(),
                                        buildValidationStamp.getName(),
                                        lastValidationRun
                                );
                            }
                        }
                );
                List<BuildPromotionLevel> promotionLevels = getBuildPromotionLevels(locale, buildId);
                DatedSignature signature = getDatedSignature(locale, EventType.BUILD_CREATED, Entity.BUILD, buildId);
                return new BranchBuild(
                        buildId,
                        t.getName(),
                        t.getDescription(),
                        signature,
                        decorations,
                        stamps,
                        promotionLevels
                );
            }
        };
    }

    private List<LocalizedDecoration> getLocalizedDecorations(final Locale locale, Entity entity, int entityId) {
        return Lists.transform(
                decorationService.getDecorations(entity, entityId),
                new Function<Decoration, LocalizedDecoration>() {
                    @Override
                    public LocalizedDecoration apply(Decoration decoration) {
                        return new LocalizedDecoration(
                                decoration.getTitle().getLocalizedMessage(strings, locale),
                                decoration.getCls(),
                                decoration.getIconPath(),
                                decoration.getLink()
                        );
                    }
                });
    }

    @Override
    @Transactional(readOnly = true)
    public BuildSummary getLastBuild(int branch) {
        TBuild t = buildDao.findLastByBranch(branch);
        if (t != null) {
            return buildSummaryFunction.apply(t);
        } else {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Integer findBuildByName(int branchId, String buildName) {
        return buildDao.findByBrandAndName(branchId, buildName);
    }

    @Override
    @Transactional
    public Ack deleteBuild(int buildId) {
        authorizationUtils.checkBuild(buildId, ProjectFunction.BUILD_DELETE);
        BuildSummary build = getBuild(buildId);
        Ack ack = buildDao.delete(buildId);
        if (ack.isSuccess()) {
            event(
                    collectEntityContext(
                            Event.of(EventType.BUILD_DELETED),
                            Entity.BRANCH,
                            build.getBranch().getId())
                            .withValue("build", build.getName())
            );
        }
        return ack;
    }

    @Override
    @Transactional
    public BuildSummary updateBuild(int buildId, BranchUpdateForm form) {
        authorizationUtils.checkBuild(buildId, ProjectFunction.BUILD_MODIFY);
        // Validation
        validate(form, NameDescription.class);
        // Loads existing build
        BuildSummary existingBuild = getBuild(buildId);
        // Query
        buildDao.updateBuild(
                buildId,
                form.getName(),
                form.getDescription()
        );
        // Audit
        event(Event.of(EventType.BUILD_UPDATED)
                .withProject(existingBuild.getBranch().getProject().getId())
                .withBranch(existingBuild.getBranch().getId())
                .withBuild(existingBuild.getId())
                .withBuild(existingBuild.getId())
        );
        // OK
        return getBuild(buildId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer findBuildAfterUsingNumericForm(int branchId, String buildName) {
        return buildDao.findBuildAfterUsingNumericForm(branchId, buildName);
    }

    @Override
    @Transactional(readOnly = true)
    public BuildSummary getBuild(int id) {
        TBuild t = buildDao.getById(id);
        return buildSummaryFunction.apply(t);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuildValidationStamp> getBuildValidationStamps(final Locale locale, final int buildId) {
        // Gets the build details
        BuildSummary build = getBuild(buildId);
        // Gets all the stamps for the branch
        List<ValidationStampSummary> stamps = getValidationStampList(build.getBranch().getId());
        // Collects information for all stamps
        return Lists.transform(
                stamps,
                new Function<ValidationStampSummary, BuildValidationStamp>() {
                    @Override
                    public BuildValidationStamp apply(ValidationStampSummary stamp) {
                        return getBuildValidationStamp(stamp, locale, buildId);
                    }
                });
    }

    protected BuildValidationStamp getBuildValidationStamp(ValidationStampSummary stamp, Locale locale, int buildId) {
        BuildValidationStamp buildStamp = BuildValidationStamp.of(stamp);
        // Gets the latest runs with their status for this build and this stamp
        List<BuildValidationStampRun> runStatuses = getValidationRuns(locale, buildId, stamp.getId());
        // OK
        return buildStamp.withRuns(runStatuses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuildPromotionLevel> getBuildPromotionLevels(final Locale locale, final int buildId) {
        // Gets all the promotion that were run for this build
        List<TPromotedRun> runs = promotedRunDao.findByBuild(buildId);
        // Reference time
        final DateTime now = TimeUtils.now();
        // Conversion
        return Lists.transform(
                runs,
                new Function<TPromotedRun, BuildPromotionLevel>() {
                    @Override
                    public BuildPromotionLevel apply(TPromotedRun t) {
                        TPromotionLevel pl = promotionLevelDao.getById(t.getPromotionLevel());
                        return new BuildPromotionLevel(
                                getPromotedRunDatedSignature(t, locale, now),
                                pl.getName(),
                                pl.getDescription(),
                                pl.getLevelNb()
                        );
                    }
                }
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ValidationRunSummary getValidationRun(int id) {
        TValidationRun t = validationRunDao.getById(id);
        int runId = t.getId();
        return new ValidationRunSummary(
                runId,
                t.getRunOrder(),
                t.getDescription(),
                getBuild(t.getBuild()),
                getValidationStamp(t.getValidationStamp()),
                getLastValidationRunStatus(runId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ValidationRunEvent> getValidationRunHistory(final Locale locale, int validationRunId, int offset, int count) {
        ValidationRunSummary validationRun = getValidationRun(validationRunId);
        int branchId = validationRun.getBuild().getBranch().getId();
        int validationStampId = validationRun.getValidationStamp().getId();
        return getValidationRunEvents(locale, validationStampId, offset, count, branchId, validationRunId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ValidationRunEvent> getValidationRunsForValidationStamp(final Locale locale, int validationStampId, int offset, int count) {
        // Gets the validation stamp
        ValidationStampSummary validationStamp = getValidationStamp(validationStampId);
        // Gets the branch id
        int branchId = validationStamp.getBranch().getId();
        // All validation runs
        int validationRunId = Integer.MAX_VALUE;
        // OK
        return getValidationRunEvents(locale, validationStampId, offset, count, branchId, validationRunId);
    }

    private List<ValidationRunEvent> getValidationRunEvents(final Locale locale, int validationStampId, int offset, int count, int branchId, int validationRunId) {
        return Lists.transform(
                validationRunEventDao.findByBranchAndValidationStamp(
                        validationRunId,
                        branchId,
                        validationStampId,
                        offset, count),
                new Function<TValidationRunEvent, ValidationRunEvent>() {
                    @Override
                    public ValidationRunEvent apply(TValidationRunEvent t) {
                        return new ValidationRunEvent(
                                getValidationRun(t.getValidationRunId()),
                                new DatedSignature(
                                        new Signature(
                                                t.getAuthorId(),
                                                t.getAuthor()
                                        ),
                                        t.getTimestamp(),
                                        TimeUtils.elapsed(strings, locale, t.getTimestamp(), TimeUtils.now(), t.getAuthor()),
                                        TimeUtils.format(locale, t.getTimestamp())
                                ),
                                t.getStatus(),
                                t.getContent()
                        );
                    }
                }
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ValidationRunStatusStub> getStatusesForLastBuilds(int validationStampId, int count) {
        // Gets the last validation runs
        List<TValidationRun> runs = validationRunDao.findLastRunsOfBuildByValidationStamp(validationStampId, count);
        // Gets the last status
        return Lists.transform(
                runs,
                new Function<TValidationRun, ValidationRunStatusStub>() {
                    @Override
                    public ValidationRunStatusStub apply(TValidationRun run) {
                        return getLastValidationRunStatus(run.getId());
                    }
                }
        );
    }

    @Override
    @Transactional
    public Ack deleteValidationRun(int validationRunId) {
        authorizationUtils.checkValidationRun(validationRunId, ProjectFunction.VALIDATION_RUN_DELETE);
        ValidationRunSummary run = getValidationRun(validationRunId);
        Ack ack = validationRunDao.deleteById(validationRunId);
        if (ack.isSuccess()) {
            event(
                    Event.of(EventType.VALIDATION_RUN_DELETED)
                            .withValue("validation_run", "#" + run.getRunOrder())
                            .withProject(run.getBuild().getBranch().getProject().getId())
                            .withBranch(run.getBuild().getBranch().getId())
                            .withBuild(run.getBuild().getId())
                            .withValidationStamp(run.getValidationStamp().getId())
            );
        }
        return ack;
    }

    // Validation run status

    @Override
    @Transactional(readOnly = true)
    public List<BuildValidationStampRun> getValidationRuns(final Locale locale, final int buildId, final int validationStampId) {
        // Lists of runs for the build and validation stamp
        List<TValidationRun> runs = validationRunDao.findByBuildAndValidationStamp(buildId, validationStampId);
        return Lists.transform(
                runs,
                new Function<TValidationRun, BuildValidationStampRun>() {
                    @Override
                    public BuildValidationStampRun apply(TValidationRun t) {
                        int runId = t.getId();
                        ValidationRunStatusStub runStatus = getLastValidationRunStatus(runId);
                        ValidationRunSummary run = getValidationRun(runId);
                        DatedSignature signature = getDatedSignature(locale, EventType.VALIDATION_RUN_CREATED, MapBuilder.of(Entity.BUILD, buildId).with(Entity.VALIDATION_STAMP, validationStampId).get());
                        return new BuildValidationStampRun(runId, run.getRunOrder(), signature, runStatus.getStatus(), runStatus.getDescription(),
                                getValidationRunHistory(locale, runId, 0, MAX_EVENTS_IN_BUILD_VALIDATION_STAMP_RUN));
                    }
                }
        );
    }

    @Override
    @Transactional
    public Ack addValidationRunComment(int runId, ValidationRunCommentCreationForm form) {
        securityUtils.checkIsLogged();
        // Properties
        List<PropertyCreationForm> properties = form.getProperties();
        if (properties != null) {
            for (PropertyCreationForm propertyForm : properties) {
                String propertyValue = propertyForm.getValue();
                if (StringUtils.isNotBlank(propertyValue)) {
                    propertiesService.saveProperty(
                            Entity.VALIDATION_RUN,
                            runId,
                            propertyForm.getExtension(),
                            propertyForm.getName(),
                            propertyValue
                    );
                }
            }
        }
        // Checks the status
        if (form.getStatus() == null) {
            // Does not do anything if empty description
            if (StringUtils.isBlank(form.getDescription())) {
                return Ack.NOK;
            }
            // No status - it means that the user creates a comment
            CommentStub comment = createComment(Entity.VALIDATION_RUN, runId, form.getDescription());
            // Registers an event for this comment
            event(
                    collectEntityContext(Event.of(EventType.VALIDATION_RUN_COMMENT), Entity.VALIDATION_RUN, runId)
                            .withComment(comment.getComment()));
            // OK
            return Ack.OK;
        } else {
            // Creates the new status
            createValidationRunStatus(runId, new ValidationRunStatusCreationForm(form.getStatus(), form.getDescription()), false);
            // OK
            return Ack.OK;
        }
    }

    @Override
    @Transactional
    public ValidationRunStatusSummary createValidationRunStatus(int validationRun, ValidationRunStatusCreationForm validationRunStatus, boolean initialStatus) {
        securityUtils.checkIsLogged();
        // Author
        Signature signature = securityUtils.getCurrentSignature();
        // Creation
        int id = validationRunStatusDao.createValidationRunStatus(
                validationRun,
                validationRunStatus.getStatus(),
                validationRunStatus.getDescription(),
                signature.getName(),
                signature.getId()
        );
        // Generates an event for the status
        // Only when additional run
        if (!initialStatus) {
            // Validation run
            ValidationRunSummary run = getValidationRun(validationRun);
            // Generates an event
            event(Event.of(EventType.VALIDATION_RUN_STATUS)
                    .withProject(run.getBuild().getBranch().getProject().getId())
                    .withBranch(run.getBuild().getBranch().getId())
                    .withBuild(run.getBuild().getId())
                    .withValidationStamp(run.getValidationStamp().getId())
                    .withValidationRun(run.getId())
                    .withValue("status", validationRunStatus.getStatus().name()));
        }
        // OK
        return new ValidationRunStatusSummary(id, signature.getName(), validationRunStatus.getStatus(), validationRunStatus.getDescription());
    }

    public ValidationRunStatusStub getLastValidationRunStatus(int validationRunId) {
        TValidationRunStatus t = validationRunStatusDao.findLastForValidationRun(validationRunId);
        return new ValidationRunStatusStub(
                t.getId(),
                t.getStatus(),
                t.getDescription()
        );
    }

    // Promoted runs

    @Override
    @Transactional(readOnly = true)
    public PromotedRunSummary getPromotedRun(int buildId, int promotionLevel) {
        TPromotedRun t = promotedRunDao.findByBuildAndPromotionLevel(buildId, promotionLevel);
        if (t != null) {
            return new PromotedRunSummary(
                    t.getId(),
                    new Signature(t.getAuthorId(), t.getAuthor()),
                    t.getCreation(),
                    t.getDescription(),
                    getBuild(t.getBuild()),
                    getPromotionLevel(t.getPromotionLevel())
            );
        } else {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Promotion> getPromotions(final Locale locale, int promotionLevelId, int offset, int count) {
        // List of `promoted_run` for this promotion level
        List<TPromotedRun> runs = promotedRunDao.findByPromotionLevel(promotionLevelId, offset, count);
        // Gets the promotion level summary
        final PromotionLevelSummary promotionLevel = getPromotionLevel(promotionLevelId);
        // Now
        final DateTime now = TimeUtils.now();
        // Converts them into Promotion objects
        return Lists.transform(
                runs,
                new Function<TPromotedRun, Promotion>() {
                    @Override
                    public Promotion apply(TPromotedRun t) {
                        return new Promotion(
                                promotionLevel,
                                getBuild(t.getBuild()),
                                getPromotedRunDatedSignature(t, locale, now)
                        );
                    }
                }
        );
    }

    @Override
    @Transactional
    public Ack removePromotedRun(int buildId, int promotionLevelId) {
        authorizationUtils.checkPromotionLevel(promotionLevelId, ProjectFunction.PROMOTION_LEVEL_DELETE);
        Ack ack = promotedRunDao.remove(buildId, promotionLevelId);
        if (ack.isSuccess()) {
            event(
                    collectEntityContext(
                            Event.of(EventType.PROMOTED_RUN_REMOVED),
                            Entity.BUILD, buildId)
                            .withPromotionLevel(promotionLevelId));
        }
        return ack;
    }

    private DatedSignature getPromotedRunDatedSignature(TPromotedRun t, Locale locale, DateTime now) {
        return getDatedSignature(
                locale,
                t.getAuthorId(), t.getAuthor(), t.getCreation(),
                now);
    }

    private DatedSignature getDatedSignature(Locale locale, Integer authorId, String author, DateTime time, DateTime now) {
        return new DatedSignature(
                new Signature(
                        authorId,
                        author
                ),
                time,
                TimeUtils.elapsed(strings, locale, time, now, author),
                TimeUtils.format(locale, time)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Promotion getEarliestPromotionForBuild(Locale locale, int buildId, int promotionLevelId) {
        // Branch for the build
        int branchId = getBuild(buildId).getBranch().getId();
        // Branch for the promotion
        PromotionLevelSummary promotionLevel = getPromotionLevel(promotionLevelId);
        int promotionBranchId = promotionLevel.getBranch().getId();
        // ... they must be same
        if (branchId != promotionBranchId) {
            throw new IllegalStateException("Branches for the build and the promotion level must be identical.");
        }
        // Looking for the earliest promoted run
        Integer earliestBuildId = promotedRunDao.findBuildByEarliestPromotion(buildId, promotionLevelId);
        // Not found
        if (earliestBuildId == null) {
            return new Promotion(
                    promotionLevel,
                    null,
                    null
            );
        } else {
            // Gets the promoted run data
            TPromotedRun t = promotedRunDao.findByBuildAndPromotionLevel(earliestBuildId, promotionLevelId);
            return new Promotion(
                    promotionLevel,
                    getBuild(earliestBuildId),
                    getPromotedRunDatedSignature(t, locale, TimeUtils.now())
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Promotion findLastPromotion(Locale locale, int promotionLevelId) {
        BuildSummary build = findLastBuildWithPromotionLevel(promotionLevelId);
        PromotionLevelSummary promotionLevel = getPromotionLevel(promotionLevelId);
        if (build != null) {
            TPromotedRun t = promotedRunDao.findByBuildAndPromotionLevel(build.getId(), promotionLevelId);
            return new Promotion(
                    promotionLevel,
                    build,
                    getPromotedRunDatedSignature(t, locale, TimeUtils.now())
            );
        } else {
            return new Promotion(
                    promotionLevel,
                    null,
                    null
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Promotion> getPromotionsForBranch(final Locale locale, int branchId, final int buildId) {
        // List of promotions for this branch
        List<PromotionLevelSummary> promotionLevelList = getPromotionLevelList(branchId);
        return Lists.transform(
                promotionLevelList,
                new Function<PromotionLevelSummary, Promotion>() {
                    @Override
                    public Promotion apply(PromotionLevelSummary promotionLevel) {
                        return getEarliestPromotionForBuild(locale, buildId, promotionLevel.getId());
                    }
                }
        );
    }

    // Comments

    @Override
    @Transactional
    public CommentStub createComment(Entity entity, int id, String content) {
        securityUtils.checkIsLogged();
        // Does not do anything if empty content
        if (StringUtils.isBlank(content)) {
            return null;
        }
        // Author
        Signature signature = securityUtils.getCurrentSignature();
        // Insertion
        int commentId = commentDao.createComment(
                entity,
                id,
                content,
                signature.getName(),
                signature.getId()
        );
        // OK
        return new CommentStub(commentId, content);
    }

    // Common

    @Override
    @Transactional(readOnly = true)
    public int getEntityId(Entity entity, String name, final Map<Entity, Integer> parentIds) {
        return entityDao.getEntityId(entity, name, parentIds);
    }

    @Override
    @Transactional(readOnly = true)
    public ChartTable getChartBranchValidationStampStatuses(int branchId) {
        List<ValidationStampSummary> stamps = getValidationStampList(branchId);
        List<String> stampNames = Lists.transform(
                stamps,
                new Function<ValidationStampSummary, String>() {
                    @Override
                    public String apply(ValidationStampSummary stamp) {
                        return stamp.getName();
                    }
                }
        );
        ChartTable table = ChartTable.create(stampNames);
        // Collects statuses for each stamp
        for (ValidationStampSummary stamp : stamps) {
            for (Status status : Status.values()) {
                int count = validationRunDao.getCountOfStatusForValidationStamp(stamp.getId(), status);
                table.put(stamp.getName(), status.name(), count);
            }
        }
        // OK
        return table;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pair<String, Double>> getChartBranchValidationStampRetries(int branchId) {
        List<ValidationStampSummary> stamps = getValidationStampList(branchId);
        Map<String, Integer> retries = new HashMap<>();
        Map<String, Integer> totals = new HashMap<>();
        for (ValidationStampSummary stamp : stamps) {
            // Indexation per build -> run -> last status
            Map<Integer, TreeMap<Integer, Status>> index = new HashMap<>();
            // Gets all the events
            List<ValidationRunEvent> events = getValidationRunsForValidationStamp(Locale.ENGLISH, stamp.getId(), 0, Integer.MAX_VALUE);
            // Inverts the list order, in order to get the events from the oldest to the newest
            events = new ArrayList<>(events);
            Collections.reverse(events);
            // Collecting each event
            for (ValidationRunEvent event : events) {
                if (event.getStatus() != null) {
                    // Build
                    int build = event.getValidationRun().getBuild().getId();
                    // Run map
                    TreeMap<Integer, Status> runMap = index.get(build);
                    if (runMap == null) {
                        runMap = new TreeMap<>();
                        index.put(build, runMap);
                    }
                    // Last status
                    runMap.put(event.getValidationRun().getRunOrder(), event.getStatus());
                }
            }
            // We have now a list of the last statuses of each run of each build
            // We now must check the last status of each last run
            // If PASSED, we must know if there were some runs before
            for (TreeMap<Integer, Status> runMap : index.values()) {
                // Total count
                Integer total = totals.get(stamp.getName());
                if (total == null) {
                    totals.put(stamp.getName(), runMap.size());
                } else {
                    totals.put(stamp.getName(), total + runMap.size());
                }
                // # of retries
                if (runMap.size() > 1) {
                    Status lastStatus = runMap.get(runMap.lastKey());
                    // If the last status is PASSED and if at least one previous run status was NOT PASSED
                    if (lastStatus == Status.PASSED &&
                            any(runMap.values(), not(equalTo(Status.PASSED)))) {
                        // We count this build as having been retried
                        Integer count = retries.get(stamp.getName());
                        if (count == null) {
                            retries.put(stamp.getName(), 1);
                        } else {
                            retries.put(stamp.getName(), count + 1);
                        }
                    }
                }
            }
        }
        // Gets the maps as pairs
        List<Pair<String, Double>> pairs = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : retries.entrySet()) {
            String stamp = entry.getKey();
            int retryCount = entry.getValue();
            int totalCount = totals.get(stamp);
            double percentage = retryCount / (double) totalCount;
            pairs.add(Pair.of(stamp, percentage));
        }
        // Sorts from the highest count to the lowest
        Collections.sort(pairs, Ordering.natural().onResultOf(new Function<Pair<String, Double>, Double>() {
            @Override
            public Double apply(Pair<String, Double> pair) {
                return pair.getRight();
            }
        }));
        // OK
        return pairs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pair<String, Integer>> getChartBranchValidationStampRunsWithoutFailure(int branchId) {
        List<ValidationStampSummary> stamps = getValidationStampList(branchId);
        List<Pair<String, Integer>> result = new ArrayList<>();
        for (ValidationStampSummary stamp : stamps) {
            // Indexation per run -> first status
            Map<Integer, Status> index = new TreeMap<>();
            // Gets all the events
            List<ValidationRunEvent> events = getValidationRunsForValidationStamp(Locale.ENGLISH, stamp.getId(), 0, Integer.MAX_VALUE);
            // Collecting each event
            for (ValidationRunEvent event : events) {
                if (event.getStatus() != null) {
                    // Indexation
                    index.put(event.getValidationRun().getId(), event.getStatus());
                }
            }
            // We have now a list of the first statuses of each run
            // Converting in pairs
            List<Pair<Integer, Status>> runs = new ArrayList<>();
            for (Map.Entry<Integer, Status> entry : index.entrySet()) {
                runs.add(Pair.of(entry.getKey(), entry.getValue()));
            }
            // Sorting from the oldest run to the newest
            Collections.sort(runs, Ordering.natural().reverse().onResultOf(new Function<Pair<Integer, Status>, Integer>() {
                @Override
                public Integer apply(Pair<Integer, Status> pair) {
                    return pair.getLeft();
                }
            }));
            // We must now count elements in this list until we reach a PASSED status
            int count = 0;
            for (Pair<Integer, Status> run : runs) {
                if (run.getRight() != Status.PASSED) {
                    break;
                } else {
                    count++;
                }
            }
            // Stores the result
            result.add(Pair.of(stamp.getName(), count));
        }
        // Sorting by count
        Collections.sort(result, Ordering.natural().onResultOf(new Function<Pair<String, Integer>, Integer>() {
            @Override
            public Integer apply(Pair<String, Integer> pair) {
                return pair.getRight();
            }
        }));
        // OK
        return result;
    }

    protected Event collectEntityContext(Event event, Entity entity, int id) {
        Event e = event.withEntity(entity, id);
        // Gets the entities in the content
        List<Entity> parentEntities = entity.getParents();
        for (Entity parentEntity : parentEntities) {
            Integer parentEntityId = entityDao.getParentEntityId(parentEntity, entity, id);
            if (parentEntityId != null) {
                e = collectEntityContext(e, parentEntity, parentEntityId);
            }
        }
        // OK
        return e;
    }

    protected Ack setImage(MultipartFile image, long maxSize, Function<byte[], Ack> imageUpdateFn) {
        // Checks the image type
        String contentType = image.getContentType();
        if (!"image/png".equals(contentType)) {
            throw new ImageIncorrectMIMETypeException(contentType, "image/png");
        }
        // Checks the size
        long imageSize = image.getSize();
        if (imageSize > maxSize) {
            throw new ImageTooBigException(imageSize, maxSize);
        }
        // Gets the bytes
        byte[] content;
        try {
            content = image.getBytes();
        } catch (IOException e) {
            throw new ImageCannotReadException(e);
        }
        // Updates the content
        return imageUpdateFn.apply(content);
    }
}
