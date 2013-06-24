package net.ontrack.service;

import java.io.File;

public interface EnvironmentService {

    File getWorkingDir(String context, String name);

}
