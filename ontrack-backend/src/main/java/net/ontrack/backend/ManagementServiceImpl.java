package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.ontrack.backend.dao.*;
import net.ontrack.backend.dao.model.*;
import net.ontrack.backend.db.SQL;
import net.ontrack.backend.db.SQLUtils;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.core.support.Each;
import net.ontrack.core.support.ItemActionWithIndex;
import net.ontrack.core.support.MapBuilder;
import net.ontrack.core.validation.NameDescription;
import net.ontrack.service.EventService;
import net.ontrack.service.ManagementService;
import net.ontrack.service.model.Event;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import javax.validation.Validator;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.lang.String.format;

@Service
public class ManagementServiceImpl extends AbstractServiceImpl implements ManagementService {
    protected final RowMapper<ValidationRunSummary> validationRunSummaryMapper = new RowMapper<ValidationRunSummary>() {
        @Override
        public ValidationRunSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("id");
            return new ValidationRunSummary(
                    id,
                    rs.getInt("run_order"),
                    rs.getString("description"),
                    getBuild(rs.getInt("build")),
                    getValidationStamp(rs.getInt("validation_stamp")),
                    getLastValidationRunStatus(id));
        }
    };
    protected final RowMapper<PromotedRunSummary> promotedRunSummaryRowMapper = new RowMapper<PromotedRunSummary>() {
        @Override
        public PromotedRunSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new PromotedRunSummary(
                    rs.getInt("id"),
                    rs.getString("description"),
                    getBuild(rs.getInt("build")),
                    getPromotionLevel(rs.getInt("promotion_level"))
            );
        }
    };
    private final SecurityUtils securityUtils;
    private final ProjectGroupDao projectGroupDao;
    private final ProjectDao projectDao;
    private final BranchDao branchDao;
    private final ValidationStampDao validationStampDao;
    private final PromotionLevelDao promotionLevelDao;
    private final BuildDao buildDao;
    private final ValidationRunStatusDao validationRunStatusDao;

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
                    getBranch(t.getBranch())
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
                    t.getDescription()
            );
        }
    };

    @Autowired
    public ManagementServiceImpl(DataSource dataSource, Validator validator, EventService auditService, SecurityUtils securityUtils, ProjectGroupDao projectGroupDao, ProjectDao projectDao, BranchDao branchDao, ValidationStampDao validationStampDao, PromotionLevelDao promotionLevelDao, BuildDao buildDao, ValidationRunStatusDao validationRunStatusDao) {
        super(dataSource, validator, auditService);
        this.securityUtils = securityUtils;
        this.projectGroupDao = projectGroupDao;
        this.projectDao = projectDao;
        this.branchDao = branchDao;
        this.validationStampDao = validationStampDao;
        this.promotionLevelDao = promotionLevelDao;
        this.buildDao = buildDao;
        this.validationRunStatusDao = validationRunStatusDao;
    }

    // Branches

    @Override
    @Transactional(readOnly = true)
    public List<ProjectGroupSummary> getProjectGroupList() {
        return Lists.transform(
                projectGroupDao.findAll(),
                new Function<TProjectGroup, ProjectGroupSummary>() {
                    @Override
                    public ProjectGroupSummary apply(TProjectGroup t) {
                        return new ProjectGroupSummary(
                                t.getId(),
                                t.getName(),
                                t.getDescription()
                        );
                    }
                }
        );
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public ProjectGroupSummary createProjectGroup(ProjectGroupCreationForm form) {
        // Validation
        validate(form, NameDescription.class);
        // Query
        int id = projectGroupDao.createGroup(form.getName(), form.getDescription());
        // Audit
        event(Event.of(EventType.PROJECT_GROUP_CREATED).withProjectGroup(id));
        // OK
        return new ProjectGroupSummary(id, form.getName(), form.getDescription());
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
    @Secured(SecurityRoles.ADMINISTRATOR)
    public ProjectSummary createProject(ProjectCreationForm form) {
        // Validation
        validate(form, NameDescription.class);
        // Query
        int id = projectDao.createProject(form.getName(), form.getDescription());
        // Audit
        event(Event.of(EventType.PROJECT_CREATED).withProject(id));
        // OK
        return new ProjectSummary(id, form.getName(), form.getDescription());
    }

    // Validation stamps

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack deleteProject(int id) {
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
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public BranchSummary createBranch(int project, BranchCreationForm form) {
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
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack deleteBranch(int branchId) {
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
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public ValidationStampSummary createValidationStamp(int branch, ValidationStampCreationForm form) {
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
        return new ValidationStampSummary(id, form.getName(), form.getDescription(), theBranch);
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack deleteValidationStamp(int validationStampId) {
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
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack imageValidationStamp(final int validationStampId, MultipartFile image) {
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
    @Secured(SecurityRoles.ADMINISTRATOR)
    public PromotionLevelSummary createPromotionLevel(int branchId, PromotionLevelCreationForm form) {
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
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack linkValidationStampToPromotionLevel(int validationStampId, int promotionLevelId) {
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
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack unlinkValidationStampToPromotionLevel(int validationStampId) {
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
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack upPromotionLevel(int promotionLevelId) {
        return promotionLevelDao.upPromotionLevel(promotionLevelId);
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack downPromotionLevel(int promotionLevelId) {
        return promotionLevelDao.downPromotionLevel(promotionLevelId);
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack imagePromotionLevel(final int promotionLevelId, MultipartFile image) {
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
    @Secured(SecurityRoles.ADMINISTRATOR)
    public PromotionLevelManagementData getPromotionLevelManagementData(int branchId) {
        // Gets the branch
        BranchSummary branch = getBranch(branchId);
        // List of validation stamps for this branch, without any promotion level
        List<ValidationStampSummary> freeValidationStampList = getValidationStampWithoutPromotionLevel(branchId);
        // List of promotion levels for this branch
        List<PromotionLevelSummary> promotionLevelList = getPromotionLevelList(branchId);
        // List of promotion levels with stamps
        List<PromotionLevelAndStamps> promotionLevelAndStampsList = Lists.transform(promotionLevelList, new Function<PromotionLevelSummary, PromotionLevelAndStamps>() {
            @Override
            public PromotionLevelAndStamps apply(PromotionLevelSummary promotionLevelSummary) {
                // Gets the list of stamps for this promotion level
                List<ValidationStampSummary> stamps = getValidationStampForPromotionLevel(promotionLevelSummary.getId());
                // OK
                return new PromotionLevelAndStamps(promotionLevelSummary).withStamps(stamps);
            }
        });
        // OK
        return new PromotionLevelManagementData(branch, freeValidationStampList, promotionLevelAndStampsList);
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
        return new BranchBuilds(
                // Validation stamps for the branch
                getValidationStampList(branch),
                // Builds for the branch and their complete status
                Lists.transform(
                        buildDao.findByBranch(branch, offset, count),
                        new Function<TBuild, BuildCompleteStatus>() {
                            @Override
                            public BuildCompleteStatus apply(TBuild t) {
                                int buildId = t.getId();
                                List<BuildValidationStamp> stamps = getBuildValidationStamps(locale, buildId);
                                List<BuildPromotionLevel> promotionLevels = getBuildPromotionLevels(locale, buildId);
                                DatedSignature signature = getDatedSignature(locale, EventType.BUILD_CREATED, Entity.BUILD, buildId);
                                return new BuildCompleteStatus(
                                        buildId,
                                        t.getName(),
                                        t.getDescription(),
                                        signature,
                                        stamps,
                                        promotionLevels);
                            }
                        }
                )
        );
    }

    // Validation runs

    @Override
    @Transactional(readOnly = true)
    public BuildSummary getBuild(int id) {
        TBuild t = buildDao.getById(id);
        return new BuildSummary(
                t.getId(),
                t.getName(),
                t.getDescription(),
                getBranch(t.getBranch())
        );
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
                        BuildValidationStamp buildStamp = BuildValidationStamp.of(stamp);
                        // Gets the latest runs with their status for this build and this stamp
                        List<BuildValidationStampRun> runStatuses = getValidationRuns(locale, buildId, stamp.getId());
                        // OK
                        return buildStamp.withRuns(runStatuses);
                    }
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuildPromotionLevel> getBuildPromotionLevels(final Locale locale, final int buildId) {
        // Gets all the promotion levels that were run for this build
        List<TPromotionLevel> tPromotionLevels = promotionLevelDao.findByBuild(buildId);
        // Conversion
        return Lists.transform(
                tPromotionLevels,
                new Function<TPromotionLevel, BuildPromotionLevel>() {
                    @Override
                    public BuildPromotionLevel apply(TPromotionLevel level) {
                        return new BuildPromotionLevel(
                                getDatedSignature(locale, EventType.PROMOTED_RUN_CREATED,
                                        MapBuilder.of(Entity.BUILD, buildId).with(Entity.PROMOTION_LEVEL, level.getId()).get()),
                                level.getName(),
                                level.getDescription(),
                                level.getLevelNb()
                        );
                    }
                }
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ValidationRunSummary getValidationRun(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.VALIDATION_RUN,
                params("id", id),
                validationRunSummaryMapper
        );
    }

    // Validation run status

    @Override
    @Transactional(readOnly = true)
    public List<BuildValidationStampRun> getValidationRuns(final Locale locale, final int buildId, final int validationStampId) {
        List<Integer> runIds = getNamedParameterJdbcTemplate().queryForList(
                SQL.VALIDATION_RUN_FOR_BUILD_AND_STAMP,
                params("build", buildId).addValue("validationStamp", validationStampId),
                Integer.class);
        return Lists.transform(runIds, new Function<Integer, BuildValidationStampRun>() {
            @Override
            public BuildValidationStampRun apply(Integer runId) {
                ValidationRunStatusStub runStatus = getLastValidationRunStatus(runId);
                ValidationRunSummary run = getValidationRun(runId);
                DatedSignature signature = getDatedSignature(locale, EventType.VALIDATION_RUN_CREATED, MapBuilder.of(Entity.BUILD, buildId).with(Entity.VALIDATION_STAMP, validationStampId).get());
                return new BuildValidationStampRun(runId, run.getRunOrder(), signature, runStatus.getStatus(), runStatus.getDescription());
            }
        });
    }

    @Override
    @Transactional
    @Secured({SecurityRoles.USER, SecurityRoles.CONTROLLER, SecurityRoles.ADMINISTRATOR})
    public Ack addValidationRunComment(int runId, ValidationRunCommentCreationForm form) {
        // Does not do anything if empty description
        if (StringUtils.isBlank(form.getDescription())) {
            return Ack.NOK;
        }
        // Checks the status
        if (StringUtils.isBlank(form.getStatus())) {
            // No status - it means that the user creates a comment
            CommentStub comment = createComment(Entity.VALIDATION_RUN, runId, form.getDescription());
            // Registers an event for this comment
            event(
                    collectEntityContext(Event.of(EventType.VALIDATION_RUN_COMMENT), Entity.VALIDATION_RUN, runId)
                            .withComment(comment.getComment()));
            // OK
            return Ack.OK;
        } else {
            // Tries to get a valid status
            Status s = Status.valueOf(form.getStatus());
            // Creates the new status
            createValidationRunStatus(runId, new ValidationRunStatusCreationForm(s, form.getDescription()), false);
            // OK
            return Ack.OK;
        }
    }

    @Override
    @Transactional
    @Secured({SecurityRoles.USER, SecurityRoles.CONTROLLER, SecurityRoles.ADMINISTRATOR})
    public ValidationRunStatusSummary createValidationRunStatus(int validationRun, ValidationRunStatusCreationForm validationRunStatus, boolean initialStatus) {
        // TODO Validation of the status
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
        try {
            return getNamedParameterJdbcTemplate().queryForObject(
                    SQL.PROMOTED_RUN,
                    params("build", buildId).addValue("promotionLevel", promotionLevel),
                    promotedRunSummaryRowMapper
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }


    // Comments

    @Override
    @Transactional
    @Secured({SecurityRoles.USER, SecurityRoles.CONTROLLER, SecurityRoles.ADMINISTRATOR})
    public CommentStub createComment(Entity entity, int id, String content) {
        // Does not do anything if empty content
        if (StringUtils.isBlank(content)) {
            return null;
        }
        // Author
        Signature signature = securityUtils.getCurrentSignature();
        // Insertion
        int commentId = dbCreate(format(SQL.COMMENT_CREATE, entity.name()),
                MapBuilder.params("content", content)
                        .with("id", id)
                        .with("author", signature.getName())
                        .with("author_id", signature.getId())
                        .with("comment_timestamp", SQLUtils.toTimestamp(SQLUtils.now()))
                        .get());
        // OK
        return new CommentStub(commentId, content);
    }

    // Common

    @Override
    @Transactional(readOnly = true)
    public int getEntityId(Entity entity, String name, final Map<Entity, Integer> parentIds) {
        final StringBuilder sql = new StringBuilder(format(
                "SELECT ID FROM %s WHERE %s = :name",
                entity.name(),
                entity.nameColumn()));
        final MapSqlParameterSource sqlParams = params("name", name);
        Each.withIndex(entity.getParents(), new ItemActionWithIndex<Entity>() {
            @Override
            public void apply(Entity parent, int index) {
                Integer parentId = parentIds.get(parent);
                sql.append(" AND ").append(parent.name()).append(" = :parent").append(index);
                sqlParams.addValue("parent" + index, parentId);
            }
        });
        Integer id = getFirstItem(sql.toString(), sqlParams, Integer.class);
        if (id == null) {
            throw new EntityNameNotFoundException(entity, name);
        } else {
            return id;
        }
    }

    protected Event collectEntityContext(Event event, Entity entity, int id) {
        Event e = event.withEntity(entity, id);
        // Gets the entities in the content
        List<Entity> parentEntities = entity.getParents();
        for (Entity parentEntity : parentEntities) {
            Integer parentEntityId = getFirstItem(
                    format("SELECT %s FROM %s WHERE ID = :id", parentEntity.name(), entity.name()),
                    params("id", id),
                    Integer.class
            );
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
        byte[] content = new byte[0];
        try {
            content = image.getBytes();
        } catch (IOException e) {
            throw new ImageCannotReadException(e);
        }
        // Updates the content
        return imageUpdateFn.apply(content);
    }
}
