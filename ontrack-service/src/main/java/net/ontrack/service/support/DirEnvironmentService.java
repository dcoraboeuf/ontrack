package net.ontrack.service.support;

import net.ontrack.service.EnvironmentService;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class DirEnvironmentService implements EnvironmentService {

    private final File homeDir;

    public DirEnvironmentService(File homeDir) {
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
