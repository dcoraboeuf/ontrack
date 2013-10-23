package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.ontrack.backend.dao.*;
import net.ontrack.backend.dao.model.*;
import net.ontrack.backend.export.ImportService;
import net.ontrack.backend.export.TExport;
import net.ontrack.backend.export.TExportedImage;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.core.support.Version;
import net.ontrack.service.ExportService;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class DefaultExportService implements ExportService {

    /**
     * Version when the export/import started to be available
     */
    public static final String REFERENCE_VERSION = "1.37";
    private final ExecutorService exportExecutorService = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setNameFormat("export-%d").setDaemon(true).build());
    private final ExecutorService importExecutorService = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setNameFormat("import-%d").setDaemon(true).build());
    private final Cache<String, ExportTask> exportCache = CacheBuilder.newBuilder().maximumSize(4).expireAfterWrite(1, TimeUnit.HOURS).build();
    private final Cache<String, ImportTask> importCache = CacheBuilder.newBuilder().maximumSize(4).expireAfterWrite(1, TimeUnit.HOURS).build();
    private final ProjectDao projectDao;
    private final BranchDao branchDao;
    private final PromotionLevelDao promotionLevelDao;
    private final ValidationStampDao validationStampDao;
    private final BuildDao buildDao;
    private final PromotedRunDao promotedRunDao;
    private final ValidationRunDao validationRunDao;
    private final ValidationRunStatusDao validationRunStatusDao;
    private final EventDao eventDao;
    private final CommentDao commentDao;
    private final PropertyDao propertyDao;
    private final BuildCleanupDao buildCleanupDao;
    private final ObjectMapper objectMapper;
    private final String version;
    /**
     * Import service for versions 1.37 and greater.
     */
    @Autowired
    @Qualifier("1.37")
    private ImportService importService137;

    @Autowired
    public DefaultExportService(ProjectDao projectDao, BranchDao branchDao, PromotionLevelDao promotionLevelDao, ValidationStampDao validationStampDao, BuildDao buildDao, PromotedRunDao promotedRunDao, ValidationRunDao validationRunDao, ValidationRunStatusDao validationRunStatusDao, EventDao eventDao, CommentDao commentDao, PropertyDao propertyDao, BuildCleanupDao buildCleanupDao, ObjectMapper objectMapper, @Value("${app.version}") String version) {
        this.projectDao = projectDao;
        this.branchDao = branchDao;
        this.promotionLevelDao = promotionLevelDao;
        this.validationStampDao = validationStampDao;
        this.buildDao = buildDao;
        this.promotedRunDao = promotedRunDao;
        this.validationRunDao = validationRunDao;
        this.validationRunStatusDao = validationRunStatusDao;
        this.eventDao = eventDao;
        this.commentDao = commentDao;
        this.propertyDao = propertyDao;
        this.buildCleanupDao = buildCleanupDao;
        this.objectMapper = objectMapper;
        this.version = version;
    }

    @Override
    @Secured(SecurityRoles.ADMINISTRATOR)
    public String exportLaunch(Collection<Integer> projectIds) {
        // UUID
        String uuid = UUID.randomUUID().toString();
        // Export task
        ExportTask task = new ExportTask(projectIds);
        // Registers the task
        exportCache.put(uuid, task);
        // Launches the export on a thread
        exportExecutorService.submit(task);
        // OK
        return uuid;
    }

    @Override
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack exportCheck(String uuid) {
        ExportTask task = exportCache.getIfPresent(uuid);
        if (task == null) {
            throw new ExportTaskNotFoundException(uuid);
        } else {
            try {
                return Ack.validate(task.checkFinished());
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new ExportException(uuid, ex);
            }
        }
    }

    @Override
    @Secured(SecurityRoles.ADMINISTRATOR)
    public ExportData exportDownload(String uuid) {
        ExportTask task = exportCache.getIfPresent(uuid);
        if (task == null) {
            throw new ExportTaskNotFoundException(uuid);
        } else {
            try {
                ExportData data = task.data();
                if (data == null) {
                    throw new ExportNotFinishedException(uuid);
                }
                return data;
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new ExportException(uuid, ex);
            }
        }
    }

    @Override
    public String importLaunch(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            ExportData importData = objectMapper.readValue(in, ExportData.class);
            return importLaunch(importData);
        } catch (IOException ex) {
            throw new ImportFormatException(file.getName(), ex);
        }
    }

    @Override
    @Secured(SecurityRoles.ADMINISTRATOR)
    public String importLaunch(ExportData importData) {
        // UUID
        String uuid = UUID.randomUUID().toString();
        // Import task
        ImportTask task = new ImportTask(importData);
        // Registers the task
        importCache.put(uuid, task);
        // Launches the export on a thread
        importExecutorService.submit(task);
        // OK
        return uuid;
    }

    @Override
    @Secured(SecurityRoles.ADMINISTRATOR)
    public ImportResult importCheck(String uuid) {
        ImportTask task = importCache.getIfPresent(uuid);
        if (task == null) {
            throw new ImportTaskNotFoundException(uuid);
        } else {
            try {
                boolean finished = task.checkFinished();
                if (finished) {
                    return task.data();
                } else {
                    return ImportResult.NOT_FINISHED;
                }
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new ImportException(uuid, ex);
            }
        }
    }

    protected ExportData doExport(Collection<Integer> projectIds) {
        // All exports
        // TODO JDK8 Processes in parallel
        return new ExportData(
                version,
                ImmutableList.copyOf(
                        Collections2.transform(
                                projectIds,
                                new Function<Integer, ProjectData>() {
                                    @Override
                                    public ProjectData apply(Integer projectId) {
                                        return exportProject(projectId);
                                    }
                                }
                        )
                )
        );
    }

    private ProjectData exportProject(int projectId) {
        // Project
        TProject project = projectDao.getById(projectId);
        // All comments & properties
        List<TComment> comments = new ArrayList<>();
        List<TProperty> properties = new ArrayList<>();
        // Branches for this project
        List<TBranch> branches = branchDao.findByProject(projectId);
        // Promotion levels for all branches
        List<TPromotionLevel> promotionLevels = new ArrayList<>();
        for (TBranch branch : branches) {
            promotionLevels.addAll(promotionLevelDao.findByBranch(branch.getId()));
        }
        // Promotion level images
        List<TExportedImage> promotionLevelImages = new ArrayList<>();
        for (TPromotionLevel promotionLevel : promotionLevels) {
            byte[] image = promotionLevelDao.getImage(promotionLevel.getId());
            if (image != null) {
                promotionLevelImages.add(new TExportedImage(promotionLevel.getId(), image));
            }
        }
        // Validation stamps for all branches
        List<TValidationStamp> validationStamps = new ArrayList<>();
        for (TBranch branch : branches) {
            validationStamps.addAll(validationStampDao.findByBranch(branch.getId()));
        }
        // Validation stamp images
        List<TExportedImage> validationStampImages = new ArrayList<>();
        for (TValidationStamp validationStamp : validationStamps) {
            byte[] image = validationStampDao.getImage(validationStamp.getId());
            if (image != null) {
                validationStampImages.add(new TExportedImage(validationStamp.getId(), image));
            }
        }
        // All builds of all branches
        List<TBuild> builds = new ArrayList<>();
        for (TBranch branch : branches) {
            builds.addAll(buildDao.findByBranch(branch.getId(), 0, Integer.MAX_VALUE));
        }
        // Promoted runs
        List<TPromotedRun> promotedRuns = new ArrayList<>();
        for (TBuild build : builds) {
            promotedRuns.addAll(promotedRunDao.findByBuild(build.getId()));
        }
        // Validation runs
        List<TValidationRun> validationRuns = new ArrayList<>();
        for (TBuild build : builds) {
            for (TValidationStamp validationStamp : validationStamps) {
                validationRuns.addAll(validationRunDao.findByBuildAndValidationStamp(build.getId(), validationStamp.getId()));
            }
        }
        // Validation run statuses
        List<TValidationRunStatus> validationRunStatuses = new ArrayList<>();
        for (TValidationRun validationRun : validationRuns) {
            validationRunStatuses.addAll(validationRunStatusDao.findByValidationRun(validationRun.getId()));
        }
        // All events for the project
        List<TEvent> events = eventDao.list(0, Integer.MAX_VALUE, Collections.singletonMap(Entity.PROJECT, projectId));
        // Comments & properties
        fetchCommentsAndProperties(comments, properties, Entity.PROJECT, projectId);
        for (TBranch branch : branches) {
            fetchCommentsAndProperties(comments, properties, Entity.BRANCH, branch.getId());
        }
        for (TPromotionLevel promotionLevel : promotionLevels) {
            fetchCommentsAndProperties(comments, properties, Entity.PROMOTION_LEVEL, promotionLevel.getId());
        }
        for (TValidationStamp validationStamp : validationStamps) {
            fetchCommentsAndProperties(comments, properties, Entity.VALIDATION_STAMP, validationStamp.getId());
        }
        for (TBuild build : builds) {
            fetchCommentsAndProperties(comments, properties, Entity.BUILD, build.getId());
        }
        for (TValidationRun validationRun : validationRuns) {
            fetchCommentsAndProperties(comments, properties, Entity.VALIDATION_RUN, validationRun.getId());
        }
        // Build cleanup policy
        List<TBuildCleanup> buildCleanups = new ArrayList<>();
        for (TBranch branch : branches) {
            TBuildCleanup buildCleanUp = buildCleanupDao.findBuildCleanUp(branch.getId());
            if (buildCleanUp != null) {
                buildCleanups.add(buildCleanUp);
            }
        }
        // Export data for the project
        TExport export = new TExport(
                project,
                branches,
                promotionLevels,
                promotionLevelImages,
                validationStamps,
                validationStampImages,
                builds,
                promotedRuns,
                validationRuns,
                validationRunStatuses,
                comments,
                properties,
                events,
                buildCleanups
        );
        // Converts to JSON
        JsonNode json = objectMapper.valueToTree(export);
        // OK
        return new ProjectData(
                project.getName(),
                json
        );
    }

    protected void fetchCommentsAndProperties(List<TComment> comments, List<TProperty> properties, Entity entity, int entityId) {
        comments.addAll(commentDao.findByEntity(entity, entityId, 0, Integer.MAX_VALUE));
        properties.addAll(propertyDao.findAll(entity, entityId));
    }

    protected ImportResult doImport(ExportData importData) {
        // Gets the version
        String importVersion = importData.getVersion();
        // Gets the import service according to the version
        ImportService importService = getImportService(importVersion);
        // For each project
        Collection<ProjectSummary> importedProjects = new ArrayList<>();
        Collection<String> rejectedProjects = new ArrayList<>();
        for (ProjectData projectData : importData.getProjects()) {
            String projectName = projectData.getName();
            TProject existingProject = projectDao.findByName(projectName);
            if (existingProject != null) {
                rejectedProjects.add(projectName);
            } else {
                importedProjects.add(
                        importService.doImport(projectData)
                );
            }
        }
        // OK
        return new ImportResult(
                importedProjects,
                rejectedProjects
        );
    }

    protected ImportService getImportService(String inputVersion) {
        // Gets rid of any SNAPSHOT extension
        // Parsing of versions
        Version sourceVersion = Version.of(StringUtils.substringBefore(inputVersion, "-SNAPSHOT"));
        // Before import ever existed...
        if (sourceVersion.compareTo(Version.of(REFERENCE_VERSION)) < 0) {
            throw new ImportVersionException(inputVersion, version);
        }
        // Major version
        else if (sourceVersion.getMajor() == 1) {
            return importService137;
        }
        // Future major versions
        else {
            throw new ImportVersionException(inputVersion, version);
        }
    }

    private abstract class ImportExportTask<R> implements Runnable {

        private final AtomicBoolean finished = new AtomicBoolean(false);
        private final AtomicReference<Exception> exception = new AtomicReference<>(null);
        private final AtomicReference<R> data = new AtomicReference<>(null);

        public boolean checkFinished() throws Exception {
            if (finished.get()) {
                Exception e = exception.get();
                if (e != null) {
                    throw e;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }

        protected void finished() {
            finished.set(true);
        }

        protected void exception(Exception ex) {
            exception.set(ex);
        }

        protected void data(R value) {
            data.set(value);
        }

        public R data() throws Exception {
            if (finished.get()) {
                Exception e = exception.get();
                if (e != null) {
                    throw e;
                } else {
                    return data.get();
                }
            } else {
                return null;
            }
        }

        @Override
        public final void run() {
            try {
                // Performs the export
                data(doTask());
            } catch (Exception ex) {
                exception(ex);
            } finally {
                finished();
            }
        }

        protected abstract R doTask();

    }

    private class ImportTask extends ImportExportTask<ImportResult> {

        private final ExportData importData;

        public ImportTask(ExportData importData) {
            this.importData = importData;
        }

        @Override
        protected ImportResult doTask() {
            return doImport(importData);
        }
    }

    private class ExportTask extends ImportExportTask<ExportData> {

        private final Collection<Integer> projectIds;

        private ExportTask(Collection<Integer> projectIds) {
            this.projectIds = projectIds;
        }

        @Override
        protected ExportData doTask() {
            return doExport(projectIds);
        }

    }
}
