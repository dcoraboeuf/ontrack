package net.ontrack.backend;

import net.ontrack.service.EnvironmentService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class DefaultEnvironmentService implements EnvironmentService {

    private final File homeDir;

    /**
     * @see net.ontrack.backend.config.EnvironmentConfig
     */
    @Autowired
    public DefaultEnvironmentService(File homeDir) {
        this.homeDir = homeDir;
    }

    @Override
    public File getWorkingDir(String context, String name) {
        File cxd = new File(homeDir, context);
        File wd = new File(cxd, name);
        try {
            FileUtils.forceMkdir(wd);
        } catch (IOException e) {
            throw new CannotCreateWorkingDirException(wd, e);
        }
        return wd;
    }

}
