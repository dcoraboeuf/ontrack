package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.ontrack.backend.db.SQL;
import net.ontrack.backend.db.SQLUtils;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.core.support.Each;
import net.ontrack.core.support.ItemActionWithIndex;
import net.ontrack.core.support.MapBuilder;
import net.ontrack.core.validation.NameDescription;
import net.ontrack.service.EventService;
import net.ontrack.service.ManagementService;
import net.ontrack.service.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
import java.util.Map;

@Service
public class ManagementServiceImpl extends AbstractServiceImpl implements ManagementService {

    @Autowired
    public ManagementServiceImpl(DataSource dataSource, Validator validator, EventService auditService) {
        super(dataSource, validator, auditService);
    }

    // Project groups

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

    // Projects

    protected final RowMapper<ProjectSummary> projectSummaryMapper = new RowMapper<ProjectSummary>() {
        @Override
        public ProjectSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ProjectSummary(rs.getInt("id"), rs.getString("name"), rs.getString("description"));
        }
    };

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

    // Branches

    protected final RowMapper<BranchSummary> branchSummaryMapper = new RowMapper<BranchSummary>() {
        @Override
        public BranchSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BranchSummary(rs.getInt("id"), rs.getString("name"), rs.getString("description"), getProject(rs.getInt("project")));
        }
    };

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

    // Validation stamps

    protected final RowMapper<ValidationStampSummary> validationStampSummaryMapper = new RowMapper<ValidationStampSummary>() {
        @Override
        public ValidationStampSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ValidationStampSummary(rs.getInt("id"), rs.getString("name"), rs.getString("description"), getBranch(rs.getInt("branch")));
        }
    };

    @Override
    @Transactional(readOnly = true)
    public List<ValidationStampSummary> getValidationStampList(int branch) {
        return getNamedParameterJdbcTemplate().query(
                SQL.VALIDATION_STAMP_LIST,
                params("branch", branch),
                validationStampSummaryMapper);
    }

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
    public Ack imageValidationStamp(int validationStampId, MultipartFile image) {
        // Checks the image type
        String contentType = image.getContentType();
        if (!"image/png".equals(contentType)) {
            throw new ImageIncorrectMIMETypeException(contentType, "image/png");
        }
        // Checks the size
        long imageSize = image.getSize();
        if (imageSize > SQL.VALIDATION_STAMP_IMAGE_MAXSIZE) {
            throw new ImageTooBigException(imageSize, SQL.VALIDATION_STAMP_IMAGE_MAXSIZE);
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
                SQL.VALIDATIONSTAMP_IMAGE_UPDATE,
                params("id", validationStampId).addValue("image", content));
        // OK
        return Ack.one(count);
    }

    @Override
    public byte[] imageValidationStamp(int validationStampId) {
        List<byte[]> list = getNamedParameterJdbcTemplate().query(
                SQL.VALIDATIONSTAMP_IMAGE,
                params("id", validationStampId),
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

    // Builds

    protected final RowMapper<BuildSummary> buildSummaryMapper = new RowMapper<BuildSummary>() {
        @Override
        public BuildSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BuildSummary(rs.getInt("id"), rs.getString("name"), rs.getString("description"), getBranch(rs.getInt("branch")));
        }
    };

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
                                return new BuildCompleteStatus(summary, stamps);
                            }
                        }
                )
        );
    }

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

    // Validation runs

    protected final RowMapper<ValidationRunSummary> validationRunSummaryMapper = new RowMapper<ValidationRunSummary>() {
        @Override
        public ValidationRunSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("id");
            return new ValidationRunSummary(
                    id,
                    rs.getInt("indexNb"),
                    rs.getString("description"),
                    getBuild(rs.getInt("build")),
                    getValidationStamp(rs.getInt("validation_stamp")),
                    getLastValidationRunStatus(id));
        }
    };

    @Override
    @Transactional(readOnly = true)
    public ValidationRunSummary getValidationRun(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.VALIDATION_RUN,
                params("id", id),
                validationRunSummaryMapper
        );
    }

    public List<BuildValidationStampRun> getValidationRuns(int buildId, int validationStampId) {
        List<Integer> runIds = getNamedParameterJdbcTemplate().queryForList(
                SQL.VALIDATION_RUN_FOR_BUILD_AND_STAMP,
                params("build", buildId).addValue("validationStamp", validationStampId),
                Integer.class);
        return Lists.transform(runIds, new Function<Integer, BuildValidationStampRun>() {
            @Override
            public BuildValidationStampRun apply(Integer runId) {
                ValidationRunStatusStub runStatus = getLastValidationRunStatus(runId);
                return new BuildValidationStampRun(runId, runStatus.getStatus(), runStatus.getDescription());
            }
        });
    }

    // Validation run status

    protected final RowMapper<ValidationRunStatusStub> validationRunStatusStubMapper = new RowMapper<ValidationRunStatusStub>() {
        @Override
        public ValidationRunStatusStub mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ValidationRunStatusStub(rs.getInt("id"), SQLUtils.getEnum(Status.class, rs, "status"), rs.getString("description"));
            // TODO Author
            // TODO Timestamp
        }
    };

    public ValidationRunStatusStub getLastValidationRunStatus(int validationRunId) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.VALIDATION_RUN_STATUS_LAST,
                params("id", validationRunId),
                validationRunStatusStubMapper);
    }

    // Common

    @Override
    @Transactional(readOnly = true)
    public int getEntityId(Entity entity, String name, final Map<Entity, Integer> parentIds) {
        final StringBuilder sql = new StringBuilder(String.format(
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

    protected String getEntityName(Entity entity, int id) {
        String sql = String.format(
                "SELECT %s FROM %s WHERE ID = :id",
                entity.nameColumn(),
                entity.name());
        String name = getFirstItem(sql, params("id", id), String.class);
        if (name == null) {
            throw new EntityIdNotFoundException(entity, id);
        } else {
            return name;
        }
    }
}
