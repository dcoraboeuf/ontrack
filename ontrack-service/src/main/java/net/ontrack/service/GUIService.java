package net.ontrack.service;

import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.ProjectSummary;

public interface GUIService {

    /**
     * Given an URI for a resource, returns the absolute URL that allows to access to it
     * through the GUI.
     */
    String toGUI(String uri);

    /**
     * Gets the GUI for a build
     */
    String getBuildGUIURL(BuildSummary build);

    /**
     * Gets the URI for a build
     */
    String getBuildURI(BuildSummary build);

    /**
     * Gets the GUI for a project
     */
    String getProjectGUIURL(ProjectSummary project);

    /**
     * Gets the URI for a project
     */
    String getProjectURI(ProjectSummary project);
}
