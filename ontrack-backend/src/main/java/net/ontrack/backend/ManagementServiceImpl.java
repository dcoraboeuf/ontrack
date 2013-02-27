package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
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
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
import java.util.Map;

import static java.lang.String.format;

@Service
public class ManagementServiceImpl extends AbstractServiceImpl implements ManagementService {

    protected final RowMapper<ProjectSummary> projectSummaryMapper = new RowMapper<ProjectSummary>() {
        @Override
        public ProjectSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ProjectSummary(rs.getInt("id"), rs.getString("name"), rs.getString("description"));
        }
    };
    protected final RowMapper<BranchSummary> branchSummaryMapper = new RowMapper<BranchSummary>() {
        @Override
        public BranchSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BranchSummary(rs.getInt("id"), rs.getString("name"), rs.getString("description"), getProject(rs.getInt("project")));
        }
    };
    protected final RowMapper<ValidationStampSummary> validationStampSummaryMapper = new RowMapper<ValidationStampSummary>() {
        @Override
        public ValidationStampSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ValidationStampSummary(rs.getInt("id"), rs.getString("name"), rs.getString("description"), getBranch(rs.getInt("branch")));
        }
    };
    protected final RowMapper<PromotionLevelSummary> promotionLevelSummaryMapper = new RowMapper<PromotionLevelSummary>() {
        @Override
        public PromotionLevelSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new PromotionLevelSummary(rs.getInt("id"), rs.getString("name"), rs.getString("description"), rs.getInt("levelNb"), getBranch(rs.getInt("branch")));
        }
    };
    protected final RowMapper<BuildSummary> buildSummaryMapper = new RowMapper<BuildSummary>() {
        @Override
        public BuildSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BuildSummary(rs.getInt("id"), rs.getString("name"), rs.getString("description"), getBranch(rs.getInt("branch")));
        }
    };
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
    protected final RowMapper<ValidationRunStatusStub> validationRunStatusStubMapper = new RowMapper<ValidationRunStatusStub>() {
        @Override
        public ValidationRunStatusStub mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ValidationRunStatusStub(rs.getInt("id"), SQLUtils.getEnum(Status.class, rs, "status"), rs.getString("description"));
            // TODO Author
            // TODO Timestamp
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

    @Autowired
    public ManagementServiceImpl(DataSource dataSource, Validator validator, EventService auditService, SecurityUtils securityUtils) {
        super(dataSource, validator, auditService);
        this.securityUtils = securityUtils;
    }

    // Branches

    @Override
    @Transactional(readOnly = true)
    public List<ProjectGroupSummary> getProjectGroupList() {
        return getJdbcTemplate().query(
                SQL.PROJECT_GROUP_LIST,
                new RowMapper<ProjectGroupSummary>() {
                    @Override
                    public ProjectGroupSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new ProjectGroupSummary(rs.getInt("id"), rs.getString("name"), rs.getString("description"));
                    }
                });
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public ProjectGroupSummary createProjectGroup(ProjectGroupCreationForm form) {
        // Validation
        validate(form, NameDescription.class);
        // Query
        int id = getNamedParameterJdbcTemplate().update(
                SQL.PROJECT_GROUP_CREATE,
                params("name", form.getName()).addValue("description", form.getDescription()));
        // Audit
        event(Event.of(EventType.PROJECT_GROUP_CREATED).withProjectGroup(id));
        // OK
        return new ProjectGroupSummary(id, form.getName(), form.getDescription());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectSummary> getProjectList() {
        return getJdbcTemplate().query(SQL.PROJECT_LIST, projectSummaryMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectSummary getProject(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.PROJECT,
                params("id", id),
                projectSummaryMapper);
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public ProjectSummary createProject(ProjectCreationForm form) {
        // Validation
        validate(form, NameDescription.class);
        // Query
        int id = dbCreate(
                SQL.PROJECT_CREATE,
                MapBuilder.params("name", form.getName()).with("description", form.getDescription()).get());
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
        Ack ack = dbDelete(SQL.PROJECT_DELETE, id);
        if (ack.isSuccess()) {
            event(Event.of(EventType.PROJECT_DELETED).withValue("project", name));
        }
        return ack;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchSummary> getBranchList(int project) {
        return getNamedParameterJdbcTemplate().query(
                SQL.BRANCH_LIST,
                params("project", project),
                branchSummaryMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BranchSummary getBranch(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.BRANCH,
                params("id", id),
                branchSummaryMapper);
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public BranchSummary createBranch(int project, BranchCreationForm form) {
        // Validation
        validate(form, NameDescription.class);
        // Query
        int id = dbCreate(
                SQL.BRANCH_CREATE,
                MapBuilder.params("project", project).with("name", form.getName()).with("description", form.getDescription()).get());
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
        Ack ack = dbDelete(SQL.BRANCH_DELETE, branchId);
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
        return getNamedParameterJdbcTemplate().query(
                SQL.VALIDATION_STAMP_LIST,
                params("branch", branch),
                validationStampSummaryMapper);
    }

    // Promotion levels

    @Override
    @Transactional(readOnly = true)
    public ValidationStampSummary getValidationStamp(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.VALIDATION_STAMP,
                params("id", id),
                validationStampSummaryMapper);
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public ValidationStampSummary createValidationStamp(int branch, ValidationStampCreationForm form) {
        // Validation
        validate(form, NameDescription.class);
        // Query
        int id = dbCreate(
                SQL.VALIDATION_STAMP_CREATE,
                MapBuilder.params("branch", branch)
                        .with("name", form.getName())
                        .with("description", form.getDescription()).get());
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
        Ack ack = dbDelete(SQL.VALIDATION_STAMP_DELETE, validationStampId);
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
    public Ack imageValidationStamp(int validationStampId, MultipartFile image) {
        return setImage(validationStampId, image, SQL.VALIDATION_STAMP_IMAGE_MAXSIZE, SQL.VALIDATIONSTAMP_IMAGE_UPDATE);

    }

    @Override
    public byte[] imageValidationStamp(int validationStampId) {
        return getImage(validationStampId, SQL.VALIDATIONSTAMP_IMAGE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionLevelSummary> getPromotionLevelList(int branch) {
        return getNamedParameterJdbcTemplate().query(
                SQL.PROMOTION_LEVEL_LIST,
                params("branch", branch),
                promotionLevelSummaryMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionLevelSummary getPromotionLevel(int promotionLevelId) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.PROMOTION_LEVEL,
                params("id", promotionLevelId),
                promotionLevelSummaryMapper);
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public PromotionLevelSummary createPromotionLevel(int branchId, PromotionLevelCreationForm form) {
        // Validation
        validate(form, NameDescription.class);
        // Count of existing promotion levels
        int count = getPromotionLevelList(branchId).size();
        int levelNb = count + 1;
        // Query
        int id = dbCreate(
                SQL.PROMOTION_LEVEL_CREATE,
                MapBuilder.params("branch", branchId)
                        .with("name", form.getName())
                        .with("description", form.getDescription())
                        .with("levelNb", levelNb)
                        .get());
        // Branch summary
        BranchSummary theBranch = getBranch(branchId);
        // Audit
        event(Event.of(EventType.PROMOTION_LEVEL_CREATED)
                .withProject(theBranch.getProject().getId())
                .withBranch(theBranch.getId())
                .withPromotionLevel(id));
        // OK
        return new PromotionLevelSummary(id, form.getName(), form.getDescription(), levelNb, theBranch);
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack linkValidationStampToPromotionLevel(int validationStampId, int promotionLevelId) {
        int count = getNamedParameterJdbcTemplate().update(
                SQL.VALIDATION_STAMP_PROMOTION_LEVEL,
                params("id", validationStampId).addValue("promotionLevel", promotionLevelId)
        );
        if (count == 1) {
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
        int count = getNamedParameterJdbcTemplate().update(
                SQL.VALIDATION_STAMP_PROMOTION_LEVEL,
                params("id", validationStampId).addValue("promotionLevel", null)
        );
        if (count == 1) {
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
        PromotionLevelSummary promotionLevel = getPromotionLevel(promotionLevelId);
        Integer higherId = getFirstItem(
                SQL.PROMOTION_LEVEL_HIGHER,
                params("branch", promotionLevel.getBranch().getId()).addValue("levelNb", promotionLevel.getLevelNb()),
                Integer.class);
        if (higherId != null) {
            return swapPromotionLevelNb(promotionLevelId, higherId);
        } else {
            return Ack.NOK;
        }
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack downPromotionLevel(int promotionLevelId) {
        PromotionLevelSummary promotionLevel = getPromotionLevel(promotionLevelId);
        Integer lowerId = getFirstItem(
                SQL.PROMOTION_LEVEL_LOWER,
                params("branch", promotionLevel.getBranch().getId()).addValue("levelNb", promotionLevel.getLevelNb()),
                Integer.class);
        if (lowerId != null) {
            return swapPromotionLevelNb(promotionLevelId, lowerId);
        } else {
            return Ack.NOK;
        }
    }

    protected Ack swapPromotionLevelNb(int aId, Integer bId) {
        // Loads the level numbers
        NamedParameterJdbcTemplate t = getNamedParameterJdbcTemplate();
        // Gets the order values
        int ordera = t.queryForInt(SQL.PROMOTION_LEVEL_LEVELNB, params("id", aId));
        int orderb = t.queryForInt(SQL.PROMOTION_LEVEL_LEVELNB, params("id", bId));
        // Changes the order
        t.update(SQL.PROMOTION_LEVEL_SET_LEVELNB, params("id", aId).addValue("levelNb", orderb));
        t.update(SQL.PROMOTION_LEVEL_SET_LEVELNB, params("id", bId).addValue("levelNb", ordera));
        // OK
        return Ack.OK;
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack imagePromotionLevel(int promotionLevelId, MultipartFile image) {
        return setImage(promotionLevelId, image, SQL.PROMOTION_LEVEL_IMAGE_MAXSIZE, SQL.PROMOTION_LEVEL_IMAGE_UPDATE);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] imagePromotionLevel(int promotionLevelId) {
        return getImage(promotionLevelId, SQL.PROMOTION_LEVEL_IMAGE);
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
        return getNamedParameterJdbcTemplate().query(
                SQL.VALIDATION_STAMP_FOR_PROMOTION_LEVEL,
                params("promotionLevel", promotionLevelId),
                validationStampSummaryMapper
        );
    }

    protected List<ValidationStampSummary> getValidationStampWithoutPromotionLevel(int branchId) {
        return getNamedParameterJdbcTemplate().query(
                SQL.VALIDATION_STAMP_WITHOUT_PROMOTION_LEVEL,
                params("branch", branchId),
                validationStampSummaryMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BranchBuilds getBuildList(int branch, int offset, int count) {
        List<BuildSummary> builds = getNamedParameterJdbcTemplate().query(
                SQL.BUILD_LIST,
                params("branch", branch).addValue("offset", offset).addValue("count", count),
                buildSummaryMapper);
        return new BranchBuilds(
                getValidationStampList(branch),
                Lists.transform(
                        builds,
                        new Function<BuildSummary, BuildCompleteStatus>() {
                            @Override
                            public BuildCompleteStatus apply(BuildSummary summary) {
                                List<BuildValidationStamp> stamps = getBuildValidationStamps(summary.getId());
                                List<PromotionLevelSummary> promotionLevels = getBuildPromotionLevels(summary.getId());
                                return new BuildCompleteStatus(summary, stamps, promotionLevels);
                            }
                        }
                )
        );
    }

    // Validation runs

    @Override
    @Transactional(readOnly = true)
    public BuildSummary getBuild(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.BUILD,
                params("id", id),
                buildSummaryMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuildValidationStamp> getBuildValidationStamps(final int buildId) {
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
                        List<BuildValidationStampRun> runStatuses = getValidationRuns(buildId, stamp.getId());
                        // OK
                        return buildStamp.withRuns(runStatuses);
                    }
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionLevelSummary> getBuildPromotionLevels(int buildId) {
        return getNamedParameterJdbcTemplate().query(
                SQL.PROMOTION_LEVEL_FOR_BUILD,
                params("build", buildId),
                promotionLevelSummaryMapper
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
    public List<BuildValidationStampRun> getValidationRuns(int buildId, int validationStampId) {
        List<Integer> runIds = getNamedParameterJdbcTemplate().queryForList(
                SQL.VALIDATION_RUN_FOR_BUILD_AND_STAMP,
                params("build", buildId).addValue("validationStamp", validationStampId),
                Integer.class);
        return Lists.transform(runIds, new Function<Integer, BuildValidationStampRun>() {
            @Override
            public BuildValidationStampRun apply(Integer runId) {
                ValidationRunStatusStub runStatus = getLastValidationRunStatus(runId);
                ValidationRunSummary run = getValidationRun(runId);
                return new BuildValidationStampRun(runId, run.getRunOrder(), runStatus.getStatus(), runStatus.getDescription());
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
            // Gets the validation run
            ValidationRunSummary run = getValidationRun(runId);
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
        int id = dbCreate(SQL.VALIDATION_RUN_STATUS_CREATE,
                MapBuilder.params("validationRun", validationRun)
                        .with("status", validationRunStatus.getStatus().name())
                        .with("description", validationRunStatus.getDescription())
                        .with("author", signature.getName())
                        .with("authorId", signature.getId())
                        .with("statusTimestamp", SQLUtils.toTimestamp(SQLUtils.now())).get());
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
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.VALIDATION_RUN_STATUS_LAST,
                params("id", validationRunId),
                validationRunStatusStubMapper);
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

    protected Ack setImage(int id, MultipartFile image, long maxSize, String imageUpdateSql) {
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
        int count = getNamedParameterJdbcTemplate().update(
                imageUpdateSql,
                params("id", id).addValue("image", content));
        // OK
        return Ack.one(count);
    }

    private byte[] getImage(int id, String sql) {
        List<byte[]> list = getNamedParameterJdbcTemplate().query(
                sql,
                params("id", id),
                new RowMapper<byte[]>() {
                    @Override
                    public byte[] mapRow(ResultSet rs, int row) throws SQLException, DataAccessException {
                        return rs.getBytes("image");
                    }
                });
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }
}
