package net.ontrack.service;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.ProjectData;

public interface ExportService {

    String exportProjectLaunch(int projectId);

    Ack exportProjectCheck(String uuid);

    ProjectData exportProjectDownload(String uuid);
}
