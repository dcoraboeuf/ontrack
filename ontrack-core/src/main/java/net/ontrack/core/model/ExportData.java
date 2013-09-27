package net.ontrack.core.model;

import lombok.Data;

import java.util.Collection;

/**
 * Data that defines the exported projects
 */
@Data
public class ExportData {

    /**
     * Version of the application at export time
     */
    private final String version;
    /**
     * List of exported projects
     */
    private final Collection<ProjectData> projects;

}
