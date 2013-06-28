package net.ontrack.backend;

import net.ontrack.service.support.DirEnvironmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class DefaultEnvironmentService extends DirEnvironmentService {

    /**
     * @see net.ontrack.backend.config.EnvironmentConfig
     */
    @Autowired
    public DefaultEnvironmentService(File homeDir) {
        super(homeDir);
    }

}
