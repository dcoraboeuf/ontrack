package net.ontrack.backend;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.ontrack.core.model.Ack;
import net.ontrack.core.model.ProjectData;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.service.ExportService;
import net.ontrack.service.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

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

    @Autowired
    public DefaultExportService(ManagementService managementService) {
        this.managementService = managementService;
    }

    @Override
    @Secured(SecurityRoles.ADMINISTRATOR)
    public String exportProjectLaunch(int projectId) {
        // UUID
        String uuid = UUID.randomUUID().toString();
        // Export task
        ExportTask task = new ExportTask(projectId);
        // Registers the task
        cache.put(uuid, task);
        // Launches the export on a thread
        executorService.submit(task);
        // OK
        return uuid;
    }

    @Override
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack exportProjectCheck(String uuid) {
        ExportTask task = cache.getIfPresent(uuid);
        if (task == null) {
            throw new ProjectExportTaskNotFoundException(uuid);
        } else {
            try {
                return Ack.validate(task.checkFinished());
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new ProjectExportException(uuid, ex);
            }
        }
    }

    @Override
    @Secured(SecurityRoles.ADMINISTRATOR)
    public ProjectData exportProjectDownload(String uuid) {
        ExportTask task = cache.getIfPresent(uuid);
        if (task == null) {
            throw new ProjectExportTaskNotFoundException(uuid);
        } else {
            try {
                ProjectData data = task.getData();
                if (data == null) {
                    throw new ProjectExportNotFinishedException(uuid);
                }
                return data;
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new ProjectExportException(uuid, ex);
            }
        }
    }

    protected ProjectData doExport(int projectId) {
        // Project summary
        ProjectSummary projectSummary = managementService.getProject(projectId);
        // OK
        return new ProjectData(projectSummary);
    }

    private class ExportTask implements Runnable {

        private final int projectId;
        private final AtomicBoolean finished = new AtomicBoolean(false);
        private final AtomicReference<Exception> exception = new AtomicReference<>(null);
        private final AtomicReference<ProjectData> data = new AtomicReference<>(null);

        private ExportTask(int projectId) {
            this.projectId = projectId;
        }

        @Override
        public void run() {
            try {
                // Performs the export
                data.set(doExport(projectId));
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

        public ProjectData getData() throws Exception {
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
