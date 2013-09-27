package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Collections2;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.ontrack.core.model.Ack;
import net.ontrack.core.model.ExportData;
import net.ontrack.core.model.ProjectData;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.service.ExportService;
import net.ontrack.service.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;
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
    private final String version;

    @Autowired
    public DefaultExportService(ManagementService managementService, @Value("${app.version}") String version) {
        this.managementService = managementService;
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

    private ProjectData exportProject(Integer projectId) {
        // Project summary
        ProjectSummary projectSummary = managementService.getProject(projectId);
        // OK
        return new ProjectData(projectSummary);
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
