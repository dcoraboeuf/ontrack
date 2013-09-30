package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Collections2;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.ontrack.backend.dao.*;
import net.ontrack.backend.dao.model.*;
import net.ontrack.backend.export.TExport;
import net.ontrack.core.model.Ack;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.ExportData;
import net.ontrack.core.model.ProjectData;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.service.ExportService;
import net.ontrack.service.ManagementService;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class DefaultExportService implements ExportService {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setNameFormat("export-%d").setDaemon(true).build());
    private final Cache<String, ExportTask> cache = CacheBuilder.newBuilder().maximumSize(4).expireAfterWrite(1, TimeUnit.HOURS).build();
    private final ManagementService managementService;
    private final ProjectDao projectDao;
    private final BranchDao branchDao;
    private final PromotionLevelDao promotionLevelDao;
    private final ValidationStampDao validationStampDao;
    private final BuildDao buildDao;
    private final EventDao eventDao;
    private final ObjectMapper objectMapper;
    private final String version;

    @Autowired
    public DefaultExportService(ManagementService managementService, ProjectDao projectDao, BranchDao branchDao, PromotionLevelDao promotionLevelDao, ValidationStampDao validationStampDao, BuildDao buildDao, EventDao eventDao, ObjectMapper objectMapper, @Value("${app.version}") String version) {
        this.managementService = managementService;
        this.projectDao = projectDao;
        this.branchDao = branchDao;
        this.promotionLevelDao = promotionLevelDao;
        this.validationStampDao = validationStampDao;
        this.buildDao = buildDao;
        this.eventDao = eventDao;
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
        cache.put(uuid, task);
        // Launches the export on a thread
        executorService.submit(task);
        // OK
        return uuid;
    }

    @Override
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack exportCheck(String uuid) {
        ExportTask task = cache.getIfPresent(uuid);
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
        ExportTask task = cache.getIfPresent(uuid);
        if (task == null) {
            throw new ExportTaskNotFoundException(uuid);
        } else {
            try {
                ExportData data = task.getData();
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

    protected ExportData doExport(Collection<Integer> projectIds) {
        // All exports
        // TODO JDK8 Processes in parallel
        return new ExportData(
                version,
                Collections2.transform(
                        projectIds,
                        new Function<Integer, ProjectData>() {
                            @Override
                            public ProjectData apply(Integer projectId) {
                                return exportProject(projectId);
                            }
                        }
                )
        );
    }

    private ProjectData exportProject(int projectId) {
        // Project
        TProject project = projectDao.getById(projectId);
        // Branches for this project
        List<TBranch> branches = branchDao.findByProject(projectId);
        // Promotion levels for all branches
        List<TPromotionLevel> promotionLevels = new ArrayList<>();
        for (TBranch branch : branches) {
            promotionLevels.addAll(promotionLevelDao.findByBranch(branch.getId()));
        }
        // Validation stamps for all branches
        List<TValidationStamp> validationStamps = new ArrayList<>();
        for (TBranch branch : branches) {
            validationStamps.addAll(validationStampDao.findByBranch(branch.getId()));
        }
        // All builds of all branches
        List<TBuild> builds = new ArrayList<>();
        for (TBranch branch : branches) {
            builds.addAll(buildDao.findByBranch(branch.getId(), 0, Integer.MAX_VALUE));
        }
        // All events for the project
        List<TEvent> events = eventDao.list(0, Integer.MAX_VALUE, Collections.singletonMap(Entity.PROJECT, projectId));
        // Export data for the project
        TExport export = new TExport(
                project,
                branches,
                promotionLevels,
                validationStamps,
                builds,
                events
        );
        // Converts to JSON
        JsonNode json = objectMapper.valueToTree(export);
        // OK
        return new ProjectData(
                managementService.getProject(projectId),
                json
        );
    }

    private class ExportTask implements Runnable {

        private final Collection<Integer> projectIds;
        private final AtomicBoolean finished = new AtomicBoolean(false);
        private final AtomicReference<Exception> exception = new AtomicReference<>(null);
        private final AtomicReference<ExportData> data = new AtomicReference<>(null);

        private ExportTask(Collection<Integer> projectIds) {
            this.projectIds = projectIds;
        }

        @Override
        public void run() {
            try {
                // Performs the export
                data.set(doExport(projectIds));
                // OK
                finished.set(true);
            } catch (Exception ex) {
                finished.set(true);
                exception.set(ex);
            }
        }

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

        public ExportData getData() throws Exception {
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
    }
}
